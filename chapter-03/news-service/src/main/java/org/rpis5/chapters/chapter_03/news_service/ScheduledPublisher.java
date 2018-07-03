package org.rpis5.chapters.chapter_03.news_service;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ScheduledPublisher<T> implements Publisher<T> {
    final ScheduledExecutorService         scheduledExecutorService;
    final int                              period;
    final TimeUnit                         unit;
    final Callable<? extends Publisher<T>> publisherCallable;

	public ScheduledPublisher(
		Callable<? extends Publisher<T>> publisherCallable,
		int period,
		TimeUnit unit
	) {
		this(publisherCallable, period, unit,
				Executors.newSingleThreadScheduledExecutor());
	}

    public ScheduledPublisher(
	    Callable<? extends Publisher<T>> publisherCallable,
	    int period,
	    TimeUnit unit,
	    ScheduledExecutorService scheduledExecutorService
    ) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.period = period;
	    this.unit = unit;
	    this.publisherCallable = publisherCallable;
    }

    @Override
    public void subscribe(Subscriber<? super T> actual) {
	    SchedulerMainSubscription<T> s = new SchedulerMainSubscription<>(actual, publisherCallable);
	    actual.onSubscribe(s);
	    s.setScheduledFuture(
            scheduledExecutorService.scheduleWithFixedDelay(s, 0, period, unit)
	    );
    }

    static final class SchedulerMainSubscription<T> implements Subscription, Runnable {

    	final Subscriber<? super T> actual;
	    final Callable<? extends Publisher<T>> publisherCallable;

	    ScheduledFuture<?> scheduledFuture;

	    volatile long requested;
	    static final AtomicLongFieldUpdater<SchedulerMainSubscription> REQUESTED =
			    AtomicLongFieldUpdater.newUpdater(SchedulerMainSubscription.class, "requested");

	    volatile boolean cancelled;

	    SchedulerMainSubscription(
            Subscriber<? super T> actual,
		    Callable<? extends Publisher<T>> publisherCallable
	    ) {
		    this.actual = actual;
		    this.publisherCallable = publisherCallable;
	    }

	    @Override
	    public void request(long n) {
		    if (n <= 0) {
			    onError(new IllegalArgumentException(
					    "Spec. Rule 3.9 - Cannot request a non strictly positive number: " + n
			    ));
			    return;
		    }

			SubscriptionUtils.request(n, this, REQUESTED);
	    }

	    @Override
	    public void cancel() {
		    if (!cancelled) {
			    cancelled = true;

		    	if (scheduledFuture!= null) {
				    scheduledFuture.cancel(true);
			    }
		    }
	    }

	    @Override
	    public void run() {
		    if (!cancelled) {
			    try {
				    Publisher<T> innerPublisher = Objects.requireNonNull(publisherCallable.call());
				    if (requested == Long.MAX_VALUE) {
					    innerPublisher.subscribe(new FastSchedulerInnerSubscriber<>(this));
				    }
				    else {
					    innerPublisher.subscribe(new SlowSchedulerInnerSubscriber<>(this));
				    }
			    }
			    catch (Exception e) {
				    onError(e);
			    }
		    }
	    }

	    void onError(Throwable throwable) {
	    	if (cancelled) {
	    		return;
		    }

			cancel();
			actual.onError(throwable);
	    }

	    void tryEmit(T e) {
		    for (;;) {
			    long r = requested;

			    if (r <= 0) {
				    onError(new IllegalStateException("Lack of demand"));
				    return;
			    }

			    if (requested == Long.MAX_VALUE ||
					    REQUESTED.compareAndSet(this, r, r - 1)) {
				    emit(e);
				    return;
			    }
		    }
	    }

	    void emit(T e) {
		    Subscriber<? super T> a = this.actual;

		    a.onNext(e);
	    }


	    void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		    this.scheduledFuture = scheduledFuture;
	    }
    }

    static abstract class SchedulerInnerSubscriber<T> implements Subscriber<T> {

    	final SchedulerMainSubscription<T> parent;

    	Subscription s;

	    SchedulerInnerSubscriber(SchedulerMainSubscription<T> parent) {
		    this.parent = parent;
	    }

	    @Override
	    public void onSubscribe(Subscription s) {
	    	if (this.s == null) {
			    this.s = s;
			    s.request(Long.MAX_VALUE);
		    } else {
	    		s.cancel();
		    }
	    }

	    @Override
	    public void onError(Throwable t) {
			parent.onError(t);
	    }

	    @Override
	    public void onComplete() {

	    }
    }

    static final class FastSchedulerInnerSubscriber<T> extends SchedulerInnerSubscriber<T> {

	    FastSchedulerInnerSubscriber(SchedulerMainSubscription<T> parent) {
		    super(parent);
	    }

	    @Override
	    public void onNext(T t) {
	    	if (parent.cancelled) {
	    		s.cancel();
	    		return;
		    }

		    parent.emit(t);
	    }
    }

    static final class SlowSchedulerInnerSubscriber<T> extends SchedulerInnerSubscriber<T> {

	    SlowSchedulerInnerSubscriber(SchedulerMainSubscription<T> parent) {
			super(parent);
		}

		@Override
		public void onNext(T t) {
			if (parent.cancelled) {
				s.cancel();
				return;
			}

			parent.tryEmit(t);
		}
	}
}

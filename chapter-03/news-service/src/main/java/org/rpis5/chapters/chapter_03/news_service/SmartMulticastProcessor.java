package org.rpis5.chapters.chapter_03.news_service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SmartMulticastProcessor implements Processor<NewsLetter, NewsLetter> {

	private static final InnerSubscription[] EMPTY      = new InnerSubscription[0];
	private static final InnerSubscription[] TERMINATED = new InnerSubscription[0];

	NewsLetter cache;
	Throwable  throwable;

	volatile     Subscription upstream;
	static final AtomicReferenceFieldUpdater<SmartMulticastProcessor, Subscription> UPSTREAM =
			AtomicReferenceFieldUpdater.newUpdater(SmartMulticastProcessor.class, Subscription.class, "upstream");

	volatile     InnerSubscription[] active = EMPTY;
	static final AtomicReferenceFieldUpdater<SmartMulticastProcessor, InnerSubscription[]> ACTIVE =
			AtomicReferenceFieldUpdater.newUpdater(SmartMulticastProcessor.class, InnerSubscription[].class, "active");


	@Override
	public void subscribe(Subscriber<? super NewsLetter> actual) {
		Objects.requireNonNull(actual);

		InnerSubscription s = new InnerSubscription(actual, this);

		if (add(s)) {
			actual.onSubscribe(s);
		}
		else {
			actual.onSubscribe(s);

			if (throwable == null) {
				s.onComplete();
			}
			else {
				s.onError(throwable);
			}
		}
	}

	@Override
	public void onSubscribe(Subscription s) {
		Objects.requireNonNull(s);

		if (UPSTREAM.compareAndSet(this, null, s)) {
			s.request(Long.MAX_VALUE);
		}
		else {
			s.cancel();
		}
	}

	@Override
	public void onNext(NewsLetter newsLetterTemplate) {
		Objects.requireNonNull(newsLetterTemplate);

		InnerSubscription[] active = this.active;
		cache = newsLetterTemplate;

		for (InnerSubscription subscription : active) {
			subscription.tryEmit(newsLetterTemplate);
		}
	}

	@Override
	public void onError(Throwable t) {
		Objects.requireNonNull(t);

		InnerSubscription[] active = ACTIVE.getAndSet(this, TERMINATED);
		throwable = t;

		for (InnerSubscription subscription : active) {
			subscription.onError(t);
		}
	}

	@Override
	public void onComplete() {
		InnerSubscription[] active = ACTIVE.getAndSet(this, TERMINATED);

		for (InnerSubscription subscription : active) {
			subscription.onComplete();
		}
	}

	private boolean add(InnerSubscription subscription) {
		for (;;) {
			InnerSubscription[] subscriptions = active;

			if (isTerminated()) {
				return false;
			}

			int n = subscriptions.length;
			InnerSubscription[] copied = new InnerSubscription[n + 1];

			if (n > 0) {
				int index = (n - 1) & hash(subscription);

				if (subscriptions[index].equals(subscription)) {
					return false;
				}

				System.arraycopy(subscriptions, 0, copied, 0, n);
			}

			copied[n] = subscription;

			if (ACTIVE.compareAndSet(this, subscriptions, copied)) {
				return true;
			}
		}
	}

	private boolean remove(InnerSubscription subscription) {
		for (;;) {
			InnerSubscription[] subscriptions = active;

			if (isTerminated()) {
				return false;
			}

			int n = subscriptions.length;

			if (n == 0) {
				return false;
			}

			InnerSubscription[] copied = new InnerSubscription[n - 1];
			int index = (n - 1) & hash(subscription);

			if (!subscriptions[index].equals(subscription)) {
				return false;
			}

			if (index > 0) {
				System.arraycopy(subscriptions, 0, copied, 0, index);
			}

			if (index + 1 < n) {
				System.arraycopy(subscriptions, index + 1, copied, index, n - index - 1);
			}

			if (ACTIVE.compareAndSet(this, subscriptions, copied)) {
				return true;
			}
		}
	}

	boolean isTerminated() {
		return active == TERMINATED;
	}

	static final int hash(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}

	private static class InnerSubscription implements Subscription {

		final Subscriber<? super NewsLetter> actual;
		final SmartMulticastProcessor        parent;

		Throwable  throwable;
		boolean    done;
		boolean    sent;

		volatile     long                                      requested;
		static final AtomicLongFieldUpdater<InnerSubscription> REQUESTED =
				AtomicLongFieldUpdater.newUpdater(InnerSubscription.class, "requested");

		volatile     int                                          wip;
		static final AtomicIntegerFieldUpdater<InnerSubscription> WIP =
				AtomicIntegerFieldUpdater.newUpdater(InnerSubscription.class, "wip");

		private InnerSubscription(
			Subscriber<? super NewsLetter> actual,
			SmartMulticastProcessor parent
		) {
			this.actual = actual;
			this.parent = parent;
		}

		@Override
		public void request(long n) {
			if (n <= 0) {
				onError(throwable = new IllegalArgumentException("negative subscription request"));
				return;
			}

			SubscriptionUtils.request(n, this, REQUESTED);

			tryDrain();
		}

		@Override
		public void cancel() {
			parent.remove(this);
			done = true;
		}

		String getRecipient() {
			if (actual instanceof NamedSubscriber) {
				return ((NamedSubscriber) actual).getName();
			}

			return null;
		}

		void onError(Throwable t) {
			if (done) {
				return;
			}

			tryDrain();
		}

		void onComplete() {
			if (done) {
				return;
			}

			parent.remove(this);

			tryDrain();
		}

		boolean isTerminated() {
			return parent.throwable != null || parent.isTerminated();
		}

		boolean hasNoMoreEmission() {
			return sent || parent.cache == null || throwable != null;
		}

		void tryEmit(NewsLetter element) {
			if (done) {
				return;
			}

			int wip;

			if ((wip = WIP.incrementAndGet(this)) > 1) {
				sent = false;
				return;
			}

			Subscriber<? super  NewsLetter> a = actual;
			long r = requested;

			for (;;) {
				if (r > 0) {
					a.onNext(element.withRecipient(getRecipient()));
					sent = true;

					REQUESTED.decrementAndGet(this);
					this.wip = 0;

					return;
				}
				else {
					wip = WIP.addAndGet(this, -wip);

					if (wip == 0) {
						sent = false;
						return;
					}
					else {
						r = requested;
					}
				}
			}
		}

		void tryDrain() {
			if (done) {
				return;
			}

			int wip;

			if ((wip = WIP.incrementAndGet(this)) > 1) {
				return;
			}

			Subscriber<? super  NewsLetter> a = actual;
			long r = requested;

			for (;;) {
				NewsLetter element = parent.cache;

				if (r > 0 && !sent && element != null) {
					a.onNext(element.withRecipient(getRecipient()));
					sent = true;

					r = REQUESTED.decrementAndGet(this);
				}

				wip = WIP.addAndGet(this, -wip);

				if (wip == 0) {
					if (!done && isTerminated() && hasNoMoreEmission()) {
						done = true;
						if (throwable == null && parent.throwable == null) {
							a.onComplete();
						}
						else {
							throwable = throwable == null ? parent.throwable : throwable;
							a.onError(throwable);
						}
					}

					return;
				}
			}
		}
	}
}

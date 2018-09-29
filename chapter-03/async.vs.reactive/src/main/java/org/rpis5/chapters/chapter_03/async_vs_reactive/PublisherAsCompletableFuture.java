package org.rpis5.chapters.chapter_03.async_vs_reactive;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PublisherAsCompletableFuture<K, T> extends CompletableFuture<T> {

    final Supplier<T>                   storage;
    final BiFunction<K, T, T>           combiner;
    final CompletionPublisherSubscriber innerSubscriber;

    PublisherAsCompletableFuture(Publisher<? extends K> publisher,
            Supplier<T> storage,
            BiFunction<K, T, T> combiner) {
        this.storage = storage;
        this.combiner = combiner;

        innerSubscriber = new CompletionPublisherSubscriber();
        publisher.subscribe(innerSubscriber);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel = super.cancel(mayInterruptIfRunning);

        if (cancel) {
            Subscription subscription = innerSubscriber.getAndSet(null);

            if (subscription != null) {
                subscription.cancel();
            }
        }

        return cancel;
    }

    class CompletionPublisherSubscriber
            extends AtomicReference<Subscription>
            implements Subscriber<K> {

        T state;

        @Override
        public void onSubscribe(Subscription s) {
            if (compareAndSet(null, s)) {
                s.request(Long.MAX_VALUE);

                state = storage.get();
            }
            else {
                s.cancel();
            }
        }

        @Override
        public void onNext(K k) {
            Objects.requireNonNull(k, "Element must be present");

            if (get() != null) {
                state = combiner.apply(k, state);
            }
        }

        @Override
        public void onError(Throwable t) {
            if (getAndSet(null) != null) {
                completeExceptionally(t);
            }
        }

        @Override
        public void onComplete() {
            if (getAndSet(null) != null) {
                complete(state);
            }
        }
    }
}

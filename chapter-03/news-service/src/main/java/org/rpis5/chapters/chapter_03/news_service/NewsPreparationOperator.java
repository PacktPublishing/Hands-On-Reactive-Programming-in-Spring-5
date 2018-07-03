package org.rpis5.chapters.chapter_03.news_service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import org.rpis5.chapters.chapter_03.news_service.dto.News;
import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class NewsPreparationOperator implements Publisher<NewsLetter> {
    final Publisher<? extends News> upstream;
    final String title;

    public NewsPreparationOperator(Publisher<? extends News> upstream, String title) {
        this.upstream = upstream;
        this.title = title;
    }

    @Override
    public void subscribe(Subscriber<? super NewsLetter> s) {
        upstream.subscribe(new NewsPreparationInner(s, title));
    }

    final static class NewsPreparationInner implements Subscription, Subscriber<News> {

        final Subscriber<? super NewsLetter> actual;
        final Queue<News>                    holder;
        final AtomicBoolean                  terminated = new AtomicBoolean();
        final String                         title;

        Subscription s;
        boolean completed;

        volatile int wip;
        static final AtomicIntegerFieldUpdater<NewsPreparationInner> WIP =
                AtomicIntegerFieldUpdater.newUpdater(NewsPreparationInner.class, "wip");

        volatile long requested;
        static final AtomicLongFieldUpdater<NewsPreparationInner> REQUESTED =
                AtomicLongFieldUpdater.newUpdater(NewsPreparationInner.class, "requested");

        NewsPreparationInner(Subscriber<? super NewsLetter> actual, String title) {
            this.actual = actual;
            this.title = title;
            this.holder = new LinkedList<>();
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (this.s == null) {
                this.s = s;
                actual.onSubscribe(this);
                s.request(Long.MAX_VALUE);
            }
            else {
                s.cancel();
            }
        }

        @Override
        public void onNext(News letter) {
            holder.offer(letter);
        }

        @Override
        public void onError(Throwable t) {
            if (!terminated.compareAndSet(false, true)) {
                return;
            }

            holder.clear();
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            completed = true;

            if (requested > 0) {
                drain();
            }
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                onError(new IllegalArgumentException("negative subscription request"));
                return;
            }

            if (terminated.get()) {
                return;
            }

            SubscriptionUtils.request(n, this, REQUESTED);
            drain();
        }

        @Override
        public void cancel() {
            if (!terminated.compareAndSet(false, true)) {
                return;
            }

            s.cancel();
            holder.clear();
        }

        void drain() {
            int wip;

            if((wip = WIP.incrementAndGet(this)) > 1) {
                return;
            }

            for (;;) {
                if (completed && terminated.compareAndSet(false, true)) {
                    NewsLetter newsLetter = NewsLetter.template()
                                                      .title(title)
                                                      .digest(new ArrayList<>(holder))
                                                      .build();
                    holder.clear();

                    actual.onNext(newsLetter);
                    actual.onComplete();
                }

                wip = WIP.addAndGet(this, -wip);

                if (wip == 0) {
                    return;
                }
            }
        }
    }
}

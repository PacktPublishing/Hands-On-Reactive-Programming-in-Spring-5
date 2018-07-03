package org.rpis5.chapters.chapter_03.news_service;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class NewsServiceSubscriber implements Subscriber<NewsLetter> {

    final Queue<NewsLetter> mailbox   = new ConcurrentLinkedQueue<>();
    final AtomicInteger     remaining = new AtomicInteger();
    final int               take;

    Subscription subscription;

    public NewsServiceSubscriber(int take) {
        this.take = take;
        this.remaining.set(take);
    }

    public void onSubscribe(Subscription s) {
        if (subscription == null) {
            subscription = s;
            subscription.request(take);
        }
        else {
            s.cancel();
        }
    }

    public void onNext(NewsLetter newsLetter) {
        Objects.requireNonNull(newsLetter);

        mailbox.offer(newsLetter);
    }

    public void onError(Throwable t) {
        Objects.requireNonNull(t);

        if (t instanceof ResubscribableErrorLettter) {
            subscription = null;
            ((ResubscribableErrorLettter) t).resubscribe(this);
        }
    }

    public void onComplete() {
        subscription = null;
    }

    public Optional<NewsLetter> eventuallyReadDigest() {
        NewsLetter letter = mailbox.poll();
        if (letter != null) {
            if (remaining.decrementAndGet() == 0) {
                subscription.request(take);
                remaining.set(take);
            }
            return Optional.of(letter);
        } return Optional.empty();
    }
}          

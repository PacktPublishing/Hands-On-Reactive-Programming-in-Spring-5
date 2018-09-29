package org.rpis5.chapters.chapter_03.async_vs_reactive;

import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class CompletableFutureAsPublisherTest extends PublisherVerification<Integer> {

    public CompletableFutureAsPublisherTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new CompletableFutureAsPublisher<>(CompletableFuture.supplyAsync(() -> 1));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1;
    }
}
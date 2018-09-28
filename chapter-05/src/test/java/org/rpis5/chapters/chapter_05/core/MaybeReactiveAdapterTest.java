package org.rpis5.chapters.chapter_05.core;

import io.reactivex.Maybe;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.core.ReactiveAdapter;

public class MaybeReactiveAdapterTest {

    final ReactiveAdapter maybeAdapter = new MaybeReactiveAdapter();

    @Test
    public void convertFromMaybeToPublisherTest() {
        Assertions.assertThat(maybeAdapter.toPublisher(Maybe.just(1)))
                  .isInstanceOf(Publisher.class);
    }

    @Test
    public void convertFromPublisherToMaybeTest() {
        Assertions.assertThat(maybeAdapter.fromPublisher(Mono.just(1)))
                  .isInstanceOf(Maybe.class);
    }
}
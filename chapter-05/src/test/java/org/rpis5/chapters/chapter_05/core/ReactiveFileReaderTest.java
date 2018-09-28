package org.rpis5.chapters.chapter_05.core;

import java.time.Duration;

import org.junit.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class ReactiveFileReaderTest {

    final ReactiveFileReader reader = new ReactiveFileReader();

    @Test
    public void readShakespeareWithBackpressureTest() {
        StepVerifier.create(reader.backpressuredShakespeare(), 1)
                    .assertNext(db -> assertThat(db.capacity()).isEqualTo(1024))
                    .expectNoEvent(Duration.ofMillis(2000))
                    .thenRequest(1)
                    .assertNext(db -> assertThat(db.capacity()).isEqualTo(1024))
                    .thenCancel()
                    .verify();
    }
}
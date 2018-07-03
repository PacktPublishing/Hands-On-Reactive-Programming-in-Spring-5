package org.rpis5.chapters.chapter_03.vert.x;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

public class MockLogService implements LogService {

    @Override
    public Publisher<String> stream() {
        return  Flowable.interval(300, TimeUnit.MILLISECONDS)
                        .map(i -> "[" + System.nanoTime() + "] [LogServiceApplication] " + "[Thread " + Thread.currentThread() + "] Some loge here " + i + "\n");
    }
}

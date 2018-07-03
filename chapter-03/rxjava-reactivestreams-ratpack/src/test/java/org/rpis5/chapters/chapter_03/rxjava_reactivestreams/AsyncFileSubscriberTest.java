package org.rpis5.chapters.chapter_03.rxjava_reactivestreams;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AsyncFileSubscriberTest {

    @Test
    public void checkWriteAllDataCorrectly() throws Exception {
        File temp = null;

        try {
            temp = File.createTempFile("checkWriteAllDataCorrectly", ".tmp");

            System.out.println("Temp file : " + temp.getAbsolutePath());
            AsyncFileSubscriber subscriber =
                    new AsyncFileSubscriber(temp.getAbsolutePath());
            Observable.interval(10, TimeUnit.MILLISECONDS)
                      .map(i -> "[" + System.nanoTime() + "] [LogServiceApplication] " + "[Thread " + Thread.currentThread() + "] Some loge here " + i + "\n")
                      .take(20)
                      .subscribe(subscriber);

            while (!subscriber.terminated) {
                Thread.yield();
            }

            long count = Files.lines(Paths.get(temp.getAbsolutePath()))
                              .count();

            Assert.assertEquals(20, count);
        }
        finally {
            if (temp != null && !temp.delete()) {
                temp.deleteOnExit();
            }
        }
    }

    @Test
    public void checkWriteAllDataCorrectlyInLowPrefetch() throws Exception {
        File temp = null;

        try {
            temp = File.createTempFile("checkWriteAllDataCorrectly", ".tmp");

            System.out.println("Temp file : " + temp.getAbsolutePath());
            AsyncFileSubscriber subscriber =
                    new AsyncFileSubscriber(temp.getAbsolutePath(),
                            Executors.newSingleThreadExecutor(), 1);
            Observable.range(0, 20)
                      .map(i -> "[" + System.nanoTime() + "] [LogServiceApplication] " + "[Thread " + Thread.currentThread() + "] Some loge here " + i + "\n")
                      .flatMap(l -> Observable.defer(() -> Observable.just(l))
                                              .subscribeOn(Schedulers.io()))
                      .subscribe(subscriber);

            while (!subscriber.terminated) {
                Thread.yield();
            }

            long count = Files.lines(Paths.get(temp.getAbsolutePath()))
                              .count();

            Assert.assertEquals(20, count);
        }
        finally {
            if (temp != null && !temp.delete()) {
                temp.deleteOnExit();
            }
        }
    }
}

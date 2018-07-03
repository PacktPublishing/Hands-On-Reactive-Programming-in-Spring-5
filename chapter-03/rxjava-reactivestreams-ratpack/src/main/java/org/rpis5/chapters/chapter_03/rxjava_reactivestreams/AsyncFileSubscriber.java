package org.rpis5.chapters.chapter_03.rxjava_reactivestreams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import lombok.SneakyThrows;
import rx.Subscriber;

public class AsyncFileSubscriber extends Subscriber<String> implements Runnable {

    final FileChannel     fileChannel;
    final ExecutorService executorService;
    final Queue<String>   queue;
    final int             prefetch;

    int remaining;

    volatile boolean terminated;

    volatile int wip;
    static final AtomicIntegerFieldUpdater<AsyncFileSubscriber> WIP =
            AtomicIntegerFieldUpdater.newUpdater(AsyncFileSubscriber.class, "wip");

    @SneakyThrows
    public AsyncFileSubscriber(String file) {
        this(file, Executors.newSingleThreadExecutor());
    }

    @SneakyThrows
    public AsyncFileSubscriber(String file, ExecutorService executorService) {
        this(file, executorService, 256);
    }

    @SneakyThrows
    public AsyncFileSubscriber(String file,
            ExecutorService executorService,
            int prefetch) {
        this.fileChannel = FileChannel.open(Paths.get(file), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        this.executorService = executorService;
        this.prefetch = prefetch;
        this.queue = new ArrayBlockingQueue<>(prefetch);
        this.remaining = prefetch;
    }

    @Override
    public void onStart() {
        request(prefetch);
    }

    @Override
    public void onCompleted() {
        terminated = true;
        trySchedule();
    }

    @Override
    public void onError(Throwable e) {
        terminated = true;
        queue.clear();
        try {
            fileChannel.close();
        }
        catch (IOException e1) {
        }
    }

    @Override
    public void onNext(String s) {
        if (terminated) {
            return;
        }

        if (!queue.offer(s)) {
           onError(new IllegalStateException());
           return;
        }

        trySchedule();
    }

    @Override
    public void run() {
        String element;
        int wip = this.wip;

        for (;;) {
            while (remaining > 0 && (element = queue.poll()) != null) {
                remaining--;
                try {
                    fileChannel.write(ByteBuffer.wrap(element.getBytes()));
                } catch (Exception e) {
                    onError(e);
                    return;
                }
            }

            if (terminated && queue.isEmpty()) {
                queue.clear();
                try {
                    fileChannel.close();
                }
                catch (IOException e1) {
                }
                return;
            }

            if (remaining == 0) {
                remaining = prefetch;
                request(prefetch);
            }

            wip = WIP.addAndGet(this, -wip);

            if (wip == 0) {
                return;
            }
        }
    }

    void trySchedule() {
        if (WIP.getAndIncrement(this) > 0) {
            return;
        }

        executorService.submit(this);
    }
}

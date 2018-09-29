package org.rpis5.chapters.chapter_04.reactor.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ThreadLocalProblemShowcase {

    public static void main(String[] args) {
        ThreadLocal<Map<Object, Object>> threadLocal = new ThreadLocal<>();
        threadLocal.set(new HashMap<>());

        Flux.range(0, 10)
            .doOnNext(k -> threadLocal.get().put(k, new Random(k).nextGaussian()))
            .publishOn(Schedulers.parallel())
            .map(k -> threadLocal.get().get(k))
            .blockLast();
    }
}

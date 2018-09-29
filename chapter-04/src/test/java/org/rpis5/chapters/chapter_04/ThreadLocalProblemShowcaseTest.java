/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_04;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ThreadLocalProblemShowcaseTest {

    @Test(expected = NullPointerException.class)
    public void shouldFailDueToDifferentThread() {
        ThreadLocal<Map<Object, Object>> threadLocal = new ThreadLocal<>();
        threadLocal.set(new HashMap<>());

        Flux.range(0, 10)
            .doOnNext(k -> threadLocal.get().put(k, new Random(k).nextGaussian()))
            .publishOn(Schedulers.parallel())
            .map(k -> threadLocal.get().get(k))
            .blockLast();
    }
}

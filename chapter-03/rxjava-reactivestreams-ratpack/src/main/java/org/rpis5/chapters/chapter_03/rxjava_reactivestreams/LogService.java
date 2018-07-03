package org.rpis5.chapters.chapter_03.rxjava_reactivestreams;

import org.reactivestreams.Publisher;

public interface LogService {
    Publisher<String> stream();
}

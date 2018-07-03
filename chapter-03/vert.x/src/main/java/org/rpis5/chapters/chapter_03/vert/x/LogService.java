package org.rpis5.chapters.chapter_03.vert.x;

import org.reactivestreams.Publisher;

public interface LogService {
    Publisher<String> stream();
}

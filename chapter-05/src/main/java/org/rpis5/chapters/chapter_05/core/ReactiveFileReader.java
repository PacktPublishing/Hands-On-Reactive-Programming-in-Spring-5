package org.rpis5.chapters.chapter_05.core;

import reactor.core.publisher.Flux;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

public class ReactiveFileReader {

    public Flux<DataBuffer> backpressuredShakespeare() {
        return DataBufferUtils
            .read(
                new DefaultResourceLoader().getResource("hamlet.txt"),
                new DefaultDataBufferFactory(),
                1024
            )
            .log();
    }

}

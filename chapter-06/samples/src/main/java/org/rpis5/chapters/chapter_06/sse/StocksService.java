package org.rpis5.chapters.chapter_06.sse;

import reactor.core.publisher.Flux;

public interface StocksService {

    Flux<StockItem> stream();
}
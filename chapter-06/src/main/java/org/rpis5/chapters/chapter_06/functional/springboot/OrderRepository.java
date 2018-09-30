package org.rpis5.chapters.chapter_06.functional.springboot;

import reactor.core.publisher.Mono;


public interface OrderRepository {

    Mono<Order> findById(String id);

    Mono<Order> save(Order order);

    Mono<Void> deleteById(String id);
}

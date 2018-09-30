package org.rpis5.chapters.chapter_06.functional.springboot;

import java.util.HashMap;
import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

@Component
public class InMemoryOrderRepository implements OrderRepository {

    final Map<String, Order> ordersMap;

    public InMemoryOrderRepository() {
        ordersMap = new HashMap<>();
    }

    @Override
    public Mono<Order> findById(String id) {
        return null;
    }

    @Override
    public Mono<Order> save(Order order) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return null;
    }
}

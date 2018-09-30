package org.rpis5.chapters.chapter_06.functional.springboot;

import java.net.URI;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Service
public class OrderHandler {

    final OrderRepository orderRepository;

    public OrderHandler(OrderRepository repository) {
        orderRepository = repository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request
            .bodyToMono(Order.class)
            .flatMap(orderRepository::save)
            .flatMap(o ->
                ServerResponse.created(URI.create("/orders/" + o.id))
                              .build()
            );
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return null;
    }
}

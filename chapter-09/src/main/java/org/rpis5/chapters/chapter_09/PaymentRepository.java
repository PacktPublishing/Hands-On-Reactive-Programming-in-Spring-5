package org.rpis5.chapters.chapter_09;

import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {

	Flux<Payment> findAllByUser(String user);
}

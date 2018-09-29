package com.example.repository;

import com.example.domain.User;

import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> findMostActive();

    Mono<User> findMostPopular();
}

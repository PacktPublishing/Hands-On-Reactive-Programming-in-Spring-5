package org.rpis5.chapters.chapter_06.security;

import reactor.core.publisher.Mono;

public class DefaultProfileService implements ProfileService {

    @Override
    public Mono<Profile> getByUser(String name) {
        return Mono.empty();
    }
}

package org.rpis5.chapters.chapter_06.security;

import reactor.core.publisher.Mono;

public interface ProfileService {

    Mono<Profile> getByUser(String name);
}

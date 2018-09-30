package org.rpis5.chapters.chapter_06.functional.password.verification.client;

import reactor.core.publisher.Mono;

public interface PasswordVerificationService {

    Mono<Void> check(String raw, String encoded);
}

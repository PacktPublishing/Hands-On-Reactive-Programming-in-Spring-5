package org.rpis5.chapters.chapter_07.rx_dbs.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Mono;

/**
 * With Reactive Redis support, we have to work directly with ReactiveRedisOperations.
 */
@RequiredArgsConstructor
public class UserSessionCache {
   private final ReactiveRedisOperations<String, UserSession> rxRedisOperations;

   public Mono<UserSession> getUSerSession(String userId) {
      return Mono.empty();
   }
}

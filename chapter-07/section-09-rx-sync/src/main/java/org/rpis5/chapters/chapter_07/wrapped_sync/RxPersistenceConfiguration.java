package org.rpis5.chapters.chapter_07.wrapped_sync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class RxPersistenceConfiguration {
   @Bean
   public Scheduler jpaScheduler() {
      return Schedulers.newParallel("JPA", 10);
   }
}

/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_10.acturator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

@Component
class BatteryStatusIndicator implements ReactiveHealthIndicator {
   private final Random rnd = new Random();

   @Override
   public Mono<Health> health() {
      return probeSensor()
         .map(this::mapStatus);
   }

   private Mono<Integer> probeSensor() {
      return Mono.fromCallable(() -> rnd.nextInt(100));
   }

   private Health mapStatus(Integer status) {
      if (status < 2) {
         return new Health.Builder()
            .outOfService()
            .withDetail("level", status)
            .build();
      } else if (status > 40) {
         return new Health.Builder()
            .up()
            .withDetail("level", status)
            .build();
      } else {
         return new Health.Builder()
            .status(new Status("Low Battery"))
            .withDetail("level", status)
            .build();
      }
   }
}
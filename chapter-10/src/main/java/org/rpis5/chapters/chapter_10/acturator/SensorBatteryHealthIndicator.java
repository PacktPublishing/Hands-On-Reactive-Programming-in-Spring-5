package org.rpis5.chapters.chapter_10.acturator;

import lombok.RequiredArgsConstructor;
import org.rpis5.chapters.chapter_10.service.TemperatureSensor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
class SensorBatteryHealthIndicator implements ReactiveHealthIndicator {
   private final TemperatureSensor temperatureSensor;

   @Override
   public Mono<Health> health() {
      return temperatureSensor
         .batteryLevel()
         .map(level -> {
            if (level > 40) {
               return new Health.Builder()
                  .up()
                  .withDetail("level", level)
                  .build();
            } else {
               return new Health.Builder()
                  .status(new Status("Low Battery"))
                  .withDetail("level", level)
                  .build();
            }
         }).onErrorResume(err -> Mono.
            just(new Health.Builder()
               .outOfService()
               .withDetail("error", err.getMessage())
               .build())
         );
   }
}
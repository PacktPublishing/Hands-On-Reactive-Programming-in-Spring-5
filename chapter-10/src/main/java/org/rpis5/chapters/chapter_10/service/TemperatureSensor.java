package org.rpis5.chapters.chapter_10.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rpis5.chapters.chapter_10.scheduler.MeteredScheduledThreadPoolExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

/**
 * Service that probes the current temperature.
 * Also, it reports operational metrics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemperatureSensor {
   private final MeterRegistry meterRegistry;

   private final Random rnd = new Random();

   private Flux<Temperature> dataStream;

   @PostConstruct
   public void init() {
      ScheduledExecutorService executor =
         new MeteredScheduledThreadPoolExecutor("temp.sensor", 3, meterRegistry);

      Scheduler eventsScheduler = Schedulers.fromExecutor(executor);
      dataStream = Flux
         .range(0, 10)
         .repeat()
         .concatMap(ignore -> this.probe()
            .delayElement(randomDelay(1000), eventsScheduler)
            .name("temperature.probe")
            .metrics()
            .log("temperature.measurement", Level.FINE))
         .publish()
         .refCount();
      log.info("Temperature Sensor is ready");
   }

   public Flux<Temperature> temperatureStream() {
      return dataStream;
   }

   public Mono<Integer> batteryLevel() {
      return Mono.fromCallable(() -> {
         int level = rnd.nextInt(100);
         if (level <= 2 ) {
            throw new RuntimeException("Can not connect to the sensor");
         }
         return level;
      });
   }

   // --- Supporting methods

   private Duration randomDelay(int maxMillis) {
      return Duration.ofMillis(rnd.nextInt(maxMillis));
   }

   private Mono<Temperature> probe() {
      return Mono.fromCallable(() -> {
         long delay = randomDelay(100).toMillis();
         try {
            Thread.sleep(delay);
            return new Temperature(15 + rnd.nextGaussian() * 10);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         } finally {
            log.info("Temperature was measured, took {} milliseconds", delay);
         }
      });
   }
}

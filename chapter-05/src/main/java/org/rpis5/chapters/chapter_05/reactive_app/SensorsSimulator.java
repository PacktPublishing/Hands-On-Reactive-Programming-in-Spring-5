package org.rpis5.chapters.chapter_05.reactive_app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Random;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorsSimulator {
   public static final int MAX_READINGS_DELAY = 5000;
   public static final int COLLECTION_MAX_SIZE = 10_000;

   private final SensorReadingRepository sensorReadingRepository;
   private final ReactiveMongoOperations mongoOperations;

   private final Random random = new Random();

   private Disposable sensorSimulationSubscription;

   @PostConstruct
   public void init() {
      initializeDb();
      sensorSimulationSubscription = simulateReadings()
         .doOnSubscribe(s -> log.info("Starting IoT sensor simulation"))
         .doOnTerminate(() -> log.info("IoT sensor simulation stopped"))
         .subscribe();
   }

   @PreDestroy
   public void cleanUp() {
      sensorSimulationSubscription.dispose();
   }

   private void initializeDb() {
      CollectionOptions collectionOptions = CollectionOptions.empty()
         .capped()
         .size(COLLECTION_MAX_SIZE);

      mongoOperations.createCollection(
          SensorsReadings.COLLECTION_NAME,
          collectionOptions
      ).block();
   }

   private Mono<Void> simulateReadings() {
      return Flux.range(0, 200)
         .repeat()
         .concatMap(i -> generateReading(i)
            .delayElement(randomDelay(MAX_READINGS_DELAY))
            .flatMap(sensorReadingRepository::save)
            .doOnNext(m -> log.info("SensorsReadings saved: {}", m)))
         .then();
   }

   private Mono<SensorsReadings> generateReading(Integer i) {
      return Mono.fromCallable(() ->
         new SensorsReadings(new ObjectId(),
             now(),
             (i % 15) + random.nextDouble() * 15,
             (i % 45) + random.nextDouble() * 45,
             (i % 10) * 0.1 + random.nextDouble() * 0.4
         )
      );
   }

   private Duration randomDelay(int maxMillis) {
      return Duration.ofMillis(random.nextInt(maxMillis));
   }
}

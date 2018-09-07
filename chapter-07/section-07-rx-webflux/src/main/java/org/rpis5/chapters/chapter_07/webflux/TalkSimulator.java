package org.rpis5.chapters.chapter_07.webflux;

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
public class TalkSimulator {
   private final MessageRepository messageRepository;
   private final ReactiveMongoOperations mongoOperations;

   private final Random rnd = new Random();

   private Disposable talkSimulationSubscription;

   @PostConstruct
   public void init() {
      initializeDb();
      talkSimulationSubscription = simulateTalk()
         .doOnSubscribe(s -> log.info("Starting talk simulation"))
         .doOnTerminate(() -> log.info("Talk simulation stopped"))
         .subscribe();
   }

   @PreDestroy
   public void cleanUp() {
      talkSimulationSubscription.dispose();
   }

   private void initializeDb() {
      CollectionOptions collectionOptions = CollectionOptions.empty()
         .capped()
         .size(10_000);

      mongoOperations.createCollection(
         "chat-messages",
         collectionOptions)
         .block();
   }

   private Mono<Void> simulateTalk() {
      return Flux.range(0, Integer.MAX_VALUE)
         .concatMap(i -> generateMessage(i)
            .delayElement(randomDelay(1000))
            .flatMap(messageRepository::save)
            .doOnNext(m -> log.info("Message saved: {}", m)))
         .then();
   }

   private Mono<Message> generateMessage(Integer i) {
      return Mono.fromCallable(() ->
         new Message(new ObjectId(), now(),
            "user-" + rnd.nextInt(9), "Message " + i));
   }

   private Duration randomDelay(int maxMillis) {
      return Duration.ofMillis(rnd.nextInt(maxMillis));
   }
}

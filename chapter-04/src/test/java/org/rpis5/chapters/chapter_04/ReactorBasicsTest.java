package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class ReactorBasicsTest {

   @Test
   @Ignore
   public void endlessStream() {
      Flux.interval(Duration.ofMillis(1))
         .collectList()
         .block();
   }

   @Test
   public void createFlux() {
      Flux<String> stream1 = Flux.just("Hello", "world");
      Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
      Flux<Integer> stream3 = Flux.range(1, 500);

      Flux<String> emptyStream = Flux.empty();
      Flux<String> streamWithError = Flux.error(new RuntimeException("Hi!"));
   }

   @Test
   public void createMono() {
      Mono<String> stream4 = Mono.just("One");
      Mono<String> stream5 = Mono.justOrEmpty(null);
      Mono<String> stream6 = Mono.justOrEmpty(Optional.empty());

      Mono<String> stream7 = Mono.fromCallable(() -> httpRequest());
      Mono<String> stream8 = Mono.fromCallable(this::httpRequest);

      StepVerifier.create(stream8)
         .expectErrorMessage("IO error")
         .verify();

      Mono<Void> noData = Mono.fromRunnable(() -> doLongAction());

      StepVerifier.create(noData)
         .expectSubscription()
         .expectNextCount(0)
         .expectComplete()
         .verify();
   }

   @Test
   public void emptyOrError() {
      Flux<String> empty = Flux.empty();
      Mono<String> error = Mono.error(new RuntimeException("Unknown id"));
   }

   @Test
   public void subscribingOnStream() throws Exception {
      Subscriber<String> subscriber = new Subscriber<String>() {
         AtomicReference<Subscription> subscription = new AtomicReference<>();
         @Override
         public void onSubscribe(Subscription s) {
            log.info("Subscribed");
            subscription.set(s);
            requestMoreData(2);
         }

         public void onNext(String s) {
            log.info("Received data: {}", s);
            requestMoreData(1);
         }

         public void onComplete() {
            log.info("Completed");
         }

         public void onError(Throwable t) { }

         private void requestMoreData(int amount) {
            log.info("Requesting more elements: {}", amount);
            subscription.get().request(amount);
         }
      };

      Flux<String> stream = Flux.just("Hello", "world", "!");
      stream.subscribe(subscriber);

      Thread.sleep(100);
   }

   @Test
   public void simpleSubscribe() {
      Flux.just("A", "B", "C")
         .subscribe(
            System.out::println,
            errorIgnored -> {},
            () -> System.out.println("Done"));
   }

   @Test
   public void mySubscriber() {
      Flux.just("A", "B", "C")
         .subscribe(new MySubscriber<>());
   }

   @Test
   public void simpleRange() {
      Flux.range(2010, 9)
         .subscribe(y -> System.out.print(y + ","));
   }

   @Test
   public void shouldCreateDefer() {
      Mono<User> userMono = requestUserData(null);
      StepVerifier.create(userMono)
         .expectNextCount(0)
         .expectErrorMessage("Invalid user id")
         .verify();
   }

   @Test
   public void startStopStreamProcessing() throws Exception {
      Mono<?> startCommand = Mono.delay(Duration.ofSeconds(1));
      Mono<?> stopCommand = Mono.delay(Duration.ofSeconds(3));
      Flux<Long> streamOfData = Flux.interval(Duration.ofMillis(100));

      streamOfData
         .skipUntilOther(startCommand)
         .takeUntilOther(stopCommand)
         .subscribe(System.out::println);

      Thread.sleep(4000);
   }

   @Test
   public void collectSort() {
      Flux.just(1, 6, 2, 8, 3, 1, 5, 1)
         .collectSortedList(Comparator.reverseOrder())
         .subscribe(System.out::println);
   }

   @Test
   public void signalProcessing() {
      Flux.range(1, 3)
         .doOnNext(e -> System.out.println("data  : " + e))
         .materialize()
         .doOnNext(e -> System.out.println("signal: " + e))
         .dematerialize()
         .collectList()
         .subscribe(r -> System.out.println("result: " + r));
   }

   @Test
   public void signalProcessingWithLog() {
      Flux.range(1, 3)
         .log("FluxEvents")
         .subscribe(e -> {}, e -> {}, () -> {}, s -> s.request(2));
   }


   public Mono<User> requestUserData(String userId) {
      return Mono.defer(() ->
         isValid(userId)
            ? Mono.fromCallable(() -> requestUser(userId))
            : Mono.error(new IllegalArgumentException("Invalid user id")));
   }

   public Mono<User> requestUserData2(String userId) {
      return isValid(userId)
         ? Mono.fromCallable(() -> requestUser(userId))
         : Mono.error(new IllegalArgumentException("Invalid user id"));
   }

   private boolean isValid(String userId) {
      return userId != null;
   }

   private void doLongAction() {
      log.info("Long action");
   }

   private User requestUser(String id){
      return new User();
   }

   private String httpRequest() {
      log.info("Making HTTP request");
      throw new RuntimeException("IO error");
   }

   public static class MySubscriber<T> extends BaseSubscriber<T> {

      public void hookOnSubscribe(Subscription subscription) {
         System.out.println("Subscribed");
         request(1);
      }

      public void hookOnNext(T value) {
         System.out.println(value);
         request(1);
      }
   }

   static class User {
      public String id, name;
   }
}

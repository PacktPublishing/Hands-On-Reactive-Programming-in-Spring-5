package org.rpis5.chapters.chapter_07.wrapped_sync;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static reactor.function.TupleUtils.function;

@Component
public class RxBookRepository extends
   ReactiveCrudRepositoryAdapter<Book, Integer, BookJpaRepository> {

   public RxBookRepository(
      BookJpaRepository delegate,
      Scheduler scheduler
   ) {
      super(delegate, scheduler);
   }

   public Flux<Book> findByIdBetween(
      Publisher<Integer> lowerPublisher,
      Publisher<Integer> upperPublisher
   ) {
      return Mono.zip(
         Mono.from(lowerPublisher),
         Mono.from(upperPublisher)
      ).flatMapMany(
         function((lower, upper) ->
            Flux
               .fromIterable(delegate.findByIdBetween(lower, upper))
               .subscribeOn(scheduler)
         ))
         .subscribeOn(scheduler);
   }

   public Flux<Book> findShortestTitle() {
      return Mono.fromCallable(delegate::findShortestTitle)
         .subscribeOn(scheduler)
         .flatMapMany(Flux::fromIterable);
   }
}

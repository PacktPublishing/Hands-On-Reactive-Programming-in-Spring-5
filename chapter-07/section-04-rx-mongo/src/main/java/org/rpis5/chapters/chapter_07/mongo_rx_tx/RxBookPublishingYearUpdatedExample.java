package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;

import static java.time.Duration.between;
import static java.time.Instant.now;
import static reactor.function.TupleUtils.function;

@Slf4j
@Component
@RequiredArgsConstructor
public class RxBookPublishingYearUpdatedExample {
   private final BookSpringDataMongoRxRepository rxBookRepository;

   public void updatedBookYearByTitle() {
      Instant start = now();
      Mono<String> title = Mono
         .delay(Duration.ofSeconds(1))
         .thenReturn("Artemis")
         .doOnSubscribe(s -> log.info("Subscribed for title"))
         .doOnNext(t -> log.info("Book title resolved: {}" , t));

      Mono<Integer> publishingYear = Mono
         .delay(Duration.ofSeconds(2))
         .thenReturn(2017)
         .doOnSubscribe(s -> log.info("Subscribed for publishing year"))
         .doOnNext(t -> log.info("New publishing year resolved: {}" , t));

      updatedBookYearByTitle(title, publishingYear)
         .doOnNext(b -> log.info("Publishing year updated for the book: {}", b))
         .hasElement()
         .doOnSuccess(status -> log.info("Updated finished {}, took: {}",
            status ? "successfully" : "unsuccessfully",
            between(start, now())))
         .subscribe();
   }

   private enum Solution {
      NAIVE_1,
      NAIVE_2,
      ZIP_TUPLE,
      ZIP_TUPLE_DOUBLE_SUBSCRIPTION,
      ZIP_FUNCTION,
      ZIP_FUNCTION_DOUBLE_SUBSCRIPTION,
      CACHED_TITLE,
      FINAL
   }

   private final Solution solution = Solution.FINAL;

   public Mono<Book> updatedBookYearByTitle(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      switch (solution) {
         case NAIVE_1:
            return updatedBookYearByTitle_1(title, newPublishingYear);
         case NAIVE_2:
            return updatedBookYearByTitle_2(title, newPublishingYear);
         case ZIP_TUPLE:
            return updatedBookYearByTitle_3(title, newPublishingYear);
         case ZIP_TUPLE_DOUBLE_SUBSCRIPTION:
            return updatedBookYearByTitle_3_2(title, newPublishingYear);
         case ZIP_FUNCTION:
            return updatedBookYearByTitle_4(title, newPublishingYear);
         case ZIP_FUNCTION_DOUBLE_SUBSCRIPTION:
            return updatedBookYearByTitle_5_error(title, newPublishingYear);
         case CACHED_TITLE:
            return updatedBookYearByTitle_6(title, newPublishingYear);
         case FINAL:
            return updatedBookYearByTitle_7(title, newPublishingYear);
         default:
            throw new RuntimeException("Unexpected solution: " + solution);
      }
   }

   private Mono<Book> updatedBookYearByTitle_1(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return rxBookRepository.findOneByTitle(title)
         .flatMap(book -> newPublishingYear
            .flatMap(year -> {
               book.setPublishingYear(year);
               return rxBookRepository.save(book);
            }));
   }

   private Mono<Book> updatedBookYearByTitle_2(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return newPublishingYear
         .flatMap(newYear -> rxBookRepository.findOneByTitle(title)
            .flatMap(book -> {
               book.setPublishingYear(newYear);
               return rxBookRepository.save(book);
            }));
   }

private Mono<Book> updatedBookYearByTitle_3(
   Mono<String> title,
   Mono<Integer> newPublishingYear
) {
   return Mono.zip(title, newPublishingYear)
      .flatMap((Tuple2<String, Integer> data) -> {
         String titleVal = data.getT1();
         Integer yearVal = data.getT2();
         return rxBookRepository
            .findOneByTitle(Mono.just(titleVal))
            .flatMap(book -> {
               book.setPublishingYear(yearVal);
               return rxBookRepository.save(book);
            });
      });
}

   private Mono<Book> updatedBookYearByTitle_3_2(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return Mono.zip(title, newPublishingYear)
         .flatMap((Tuple2<String, Integer> data) ->
            rxBookRepository
               .findOneByTitle(title)
               .flatMap(book -> {
                  Integer y = data.getT2();
                  book.setPublishingYear(y);
                  return rxBookRepository.save(book);
               }));
   }

   private Mono<Book> updatedBookYearByTitle_4(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return Mono.zip(title, newPublishingYear)
         .flatMap(function((titleValue, yearValue) ->
               rxBookRepository
                  .findOneByTitle(Mono.just(titleValue))
                  .flatMap(book -> {
                     book.setPublishingYear(yearValue);
                     return rxBookRepository.save(book);
                  })));
   }

   private Mono<Book> updatedBookYearByTitle_5_error(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return Mono.zip(title, newPublishingYear)
         .flatMap(
            function((t, y) -> rxBookRepository
               .findOneByTitle(title)
               .flatMap(book -> {
                  book.setPublishingYear(y);
                  return rxBookRepository.save(book);
               })));
   }

   private Mono<Book> updatedBookYearByTitle_6(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return Mono.defer(() -> {
         Mono<String> cachedTitle = title.cache();
         return Mono.zip(cachedTitle, newPublishingYear)
            .flatMap(
               function((t, y) -> rxBookRepository
                  .findOneByTitle(cachedTitle)
                  .flatMap(book -> {
                     book.setPublishingYear(y);
                     return rxBookRepository.save(book);
                  })));
      });
   }

   private Mono<Book> updatedBookYearByTitle_7(
      Mono<String> title,
      Mono<Integer> newPublishingYear
   ) {
      return Mono.zip(
         newPublishingYear,
         rxBookRepository.findOneByTitle(title)
      ).flatMap(function((yearValue, bookValue) -> {
         bookValue.setPublishingYear(yearValue);
         return rxBookRepository.save(bookValue);
      }));
   }
}

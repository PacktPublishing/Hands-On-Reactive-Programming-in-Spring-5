package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookSpringDataMongoRxRepository
   extends ReactiveMongoRepository<Book, ObjectId> {

   Mono<Book> findOneByTitle(Mono<String> title);

   Flux<Book> findManyByTitleRegex(String regexp);

   @Meta(maxScanDocuments = 3)
   Flux<Book> findByAuthorsOrderByPublishingYearDesc(Publisher<String> authors);

   @Query("{ 'authors.1': { $exists: true } }")
   Flux<Book> booksWithFewAuthors();

   Flux<Book> findByPublishingYearBetweenOrderByPublishingYear(
      Integer from,
      Integer to,
      Pageable pageable
   );
}


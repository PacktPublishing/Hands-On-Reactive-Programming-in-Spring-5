/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.mongo_rx_repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BookSpringDataMongoRxRepository
   extends ReactiveMongoRepository<Book, Integer> {

   Flux<Book> findByAuthorsOrderByPublishingYearDesc(Flux<String> authors);

   @Query("{ 'authors.1': { $exists: true } }")
   Flux<Book> booksWithFewAuthors();
}


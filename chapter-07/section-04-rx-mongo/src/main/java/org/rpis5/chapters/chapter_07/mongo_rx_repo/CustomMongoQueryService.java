/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.mongo_rx_repo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomMongoQueryService {
   private final ReactiveMongoTemplate mongoTemplate;

   public Flux<Book> getAllBooksForSearch(String arg) {
      return null;
   }
}

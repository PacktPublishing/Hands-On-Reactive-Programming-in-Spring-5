package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class RxMongoTemplateQueryService {
   private static final String BOOK_COLLECTION = "book";

   private final ReactiveMongoOperations mongoOperations;

   public Flux<Book> findBooksByTitle(String title) {
      Query query = Query.query(new Criteria("title")
         .regex(".*" + title + ".*"))
         .limit(100);
      return mongoOperations.find(query, Book.class, BOOK_COLLECTION);
   }
}

package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Service
public class RxMongoDriverQueryService {
   private static final String BOOK_COLLECTION = "book";

   private final MongoClient mongoClient;
   private final String dbName;

   public RxMongoDriverQueryService(
      MongoClient mongoClient,
      @Value("${spring.data.mongodb.database}") String dbName
   ) {
      this.mongoClient = mongoClient;
      this.dbName = dbName;
   }

   public Flux<Book> findBooksByTitle(String title, boolean negate) {
      return Flux.defer(() -> {
         Bson query = Filters
            .regex("title", ".*" + title + ".*");

         if (negate) {
            query = Filters.not(query);
         }
         return mongoClient
            .getDatabase(dbName)
            .getCollection(BOOK_COLLECTION)
            .find(query);
      })
         .map(doc -> new Book(
            doc.getObjectId("id"),
            doc.getString("title"),
            doc.getInteger("pubYear"),
            Collections.emptyList() // Omit authors deserialization for the sake of simplicity
         ));
   }
}

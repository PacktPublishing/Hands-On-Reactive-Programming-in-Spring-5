/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.mongo_rx_repo;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static com.mongodb.client.model.Filters.regex;

@Service
@RequiredArgsConstructor
public class RxMongoDriverQueryService {
   private static final String BOOK_COLLECTION = "book";

   private final MongoClient mongoClient;

   public Flux<Book> findBooksByTitle(String title, boolean negative) {
      MongoDatabase database = mongoClient.getDatabase("mydb");
      MongoCollection<Document> collection = database.getCollection(BOOK_COLLECTION);

      return Flux.defer(() -> {
         Bson query = regex("title", ".*" + title + ".*");
         return collection.find(query);
      })
         .map(doc -> new Book(
            doc.getObjectId("id"),
            doc.getString("title"),
            doc.getInteger("pubYear"),
            Collections.emptyList() // Omit authors
         ));
   }
}

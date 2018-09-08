package org.rpis5.chapters.chapter_07.webflux;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface MessageRepository
   extends ReactiveMongoRepository<Message, ObjectId> {

   @Tailable
   Flux<Message> findBy();

   @Tailable
   Flux<Message> findByUser(String user);
}

package org.rpis5.chapters.chapter_05.reactive_app;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface SensorReadingRepository
   extends ReactiveMongoRepository<SensorsReadings, ObjectId> {

   @Tailable
   Flux<SensorsReadings> findBy();
}

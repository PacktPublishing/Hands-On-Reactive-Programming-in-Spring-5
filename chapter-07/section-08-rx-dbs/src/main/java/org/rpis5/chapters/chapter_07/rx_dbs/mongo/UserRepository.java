package org.rpis5.chapters.chapter_07.rx_dbs.mongo;

import org.rpis5.chapters.chapter_07.rx_dbs.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Example of ReactiveMongoRepository
 */
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}

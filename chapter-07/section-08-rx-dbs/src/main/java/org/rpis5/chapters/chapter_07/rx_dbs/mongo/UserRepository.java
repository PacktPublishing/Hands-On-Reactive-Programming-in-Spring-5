package org.rpis5.chapters.chapter_07.rx_dbs.mongo;

import org.rpis5.chapters.chapter_07.rx_dbs.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

// TODO: Add something specific!!!
// TODO: Capped Collections and Tailable Cursors
// TODO: Some example: http://claudioed.tech/2018/03/07/continuous-query-with-spring-data-reactive-mongodb/
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}

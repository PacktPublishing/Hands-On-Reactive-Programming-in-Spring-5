package org.rpis5.chapters.chapter_07.rx_dbs.couchbase;

import org.rpis5.chapters.chapter_07.rx_dbs.User;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;

// TODO: Add something specific!!!
public interface UserRepository extends ReactiveCouchbaseRepository<User, String> {
}

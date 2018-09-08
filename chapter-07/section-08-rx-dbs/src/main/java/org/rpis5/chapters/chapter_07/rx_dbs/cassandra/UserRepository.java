package org.rpis5.chapters.chapter_07.rx_dbs.cassandra;

import org.rpis5.chapters.chapter_07.rx_dbs.User;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

// TODO: Add something specific!!!
public interface UserRepository extends ReactiveCassandraRepository<User, String> {
}

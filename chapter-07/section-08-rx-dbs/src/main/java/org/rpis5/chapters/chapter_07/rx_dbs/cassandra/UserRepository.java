/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.rx_dbs.cassandra;

import org.rpis5.chapters.chapter_07.rx_dbs.User;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

// TODO: Add something specific!!!
public interface UserRepository extends ReactiveCassandraRepository<User, String> {
}

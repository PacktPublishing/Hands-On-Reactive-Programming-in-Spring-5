/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.rx_dbs.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;

// TODO: Add something specific logic here!!!
// TODO: Add something about Pub-Sub
// TODO: Add something ....
@RequiredArgsConstructor
public class UserSessionCache {
   private final ReactiveRedisOperations<String, UserSession> rxRedisOperations;

}

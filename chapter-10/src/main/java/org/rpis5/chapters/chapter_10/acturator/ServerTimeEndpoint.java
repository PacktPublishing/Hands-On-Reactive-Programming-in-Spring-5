/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_10.acturator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

@Component
@Endpoint(id = "server-time")
public class ServerTimeEndpoint {

   @SuppressWarnings("unused")
   @ReadOperation
   public Mono<Map<String, Object>> reportServerTime() {
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("currentTime", Instant.now().toString());
      response.put("currentTimeMillis", System.currentTimeMillis());
      response.put("timeZoneName",
         TimeZone.getDefault().getDisplayName());
      response.put("timeZoneId", TimeZone.getDefault().getID());
      return Mono.just(response);
   }
}

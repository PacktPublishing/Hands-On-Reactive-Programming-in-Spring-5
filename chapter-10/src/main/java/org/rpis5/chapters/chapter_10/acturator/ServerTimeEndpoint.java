package org.rpis5.chapters.chapter_10.acturator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@Endpoint(id = "server-time")
public class ServerTimeEndpoint {
   public static final String TIME_SERVER = "0.ua.pool.ntp.org";

   @SuppressWarnings("unused")
   @ReadOperation
   public Mono<Map<String, Object>> reportServerTime() {
      return getNtpTimeOffset()
         .map(timeOffset -> {
            Map<String, Object> rsp = new LinkedHashMap<>();
            rsp.put("serverTime", Instant.now().toString());
            rsp.put("ntpOffsetMillis", timeOffset);
            return rsp;
         });
   }

   private Mono<Long> getNtpTimeOffset() {
      return Mono.fromCallable(() -> {
         Instant start = Instant.now();
         NTPUDPClient timeClient = null;
         try {
            timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            timeInfo.computeDetails();
            return timeInfo.getOffset();
         } finally {
            if (timeClient != null) {
               timeClient.close();
               log.info("NTP time retrieved, took: {}",
                  Duration.between(start, Instant.now()));
            }
         }

      }).subscribeOn(Schedulers.elastic());
   }
}

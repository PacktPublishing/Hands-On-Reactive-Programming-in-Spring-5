package org.rpis5.chapters.chapter_07.r2dbs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class PostgresConfiguration {
   private PostgreSQLContainer postgres;

   @Bean
   public DatabaseLocation databaseLocation() throws InterruptedException {
      postgres = new PostgreSQLContainer();
      postgres.waitingFor(Wait
         .forListeningPort()
         .withStartupTimeout(Duration.ofSeconds(10)));
      postgres.withLogConsumer(new Consumer<OutputFrame>() {
         @Override
         public void accept(OutputFrame outputFrame) {
            log.info("[PgSQL]: {}", outputFrame.getUtf8String().trim());
         }
      });
      postgres.start();

      // TODO: Use some better wait strategy
      Thread.sleep(10_000);

      DatabaseLocation dbLocation = new DatabaseLocation(
         "localhost",
         postgres.getFirstMappedPort(),
         postgres.getDatabaseName(),
         postgres.getUsername(),
         postgres.getPassword());

      log.info("Database location: {}", dbLocation);

      return dbLocation;
   }

   @PreDestroy
   public void cleanUp() {
      postgres.stop();
   }
}

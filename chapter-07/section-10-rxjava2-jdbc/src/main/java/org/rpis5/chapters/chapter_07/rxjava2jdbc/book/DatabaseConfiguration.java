package org.rpis5.chapters.chapter_07.rxjava2jdbc.book;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.davidmoten.rx.jdbc.Database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class DatabaseConfiguration {
   @Bean
   public Database database(
      @Value("${spring.datasource.url}") String uri,
      @Value("${rxjava2jdbc.pool.size}") Integer poolSize
   ) {
      Database db = Database
         .from(uri, poolSize);

      initializeDatabase(db)
         .block();

      return db;
   }

   private Mono<Void> initializeDatabase(Database database) {
      return Mono.fromCallable(() -> {
         String schema =
            Resources.toString(Resources.getResource("schema.sql"), Charsets.UTF_8);

         String data =
            Resources.toString(Resources.getResource("data.sql"), Charsets.UTF_8);

         return database.update(schema)
            .counts()
            .ignoreElements()
            .andThen(database
               .update(data)
               .counts())
            .blockingLast();
      }).then();
   }
}

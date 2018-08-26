/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.r2dbs;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.function.TransactionalDatabaseClient;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

/**
 * @author Oliver Gierke
 */
@Configuration
class InfrastructureConfiguration {

   @Bean
   BookRepository customerRepository(R2dbcRepositoryFactory factory) {
      return factory.getRepository(BookRepository.class);
   }

   @Bean
   R2dbcRepositoryFactory repositoryFactory(DatabaseClient client) {

      RelationalMappingContext context = new RelationalMappingContext();
      return new R2dbcRepositoryFactory(client, context);
   }

   @Bean
   TransactionalDatabaseClient databaseClient(ConnectionFactory factory) {
      return TransactionalDatabaseClient.builder()
         .connectionFactory(factory)
         .build();
   }

   @Bean
   PostgresqlConnectionFactory connectionFactory(DatabaseLocation databaseLocation) {

      PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
         .host(databaseLocation.getHost())
         .port(databaseLocation.getPort())
         .database(databaseLocation.getDatabase())
         .username(databaseLocation.getUser())
         .password(databaseLocation.getPassword())
         .build();

      return new PostgresqlConnectionFactory(config);
   }
}

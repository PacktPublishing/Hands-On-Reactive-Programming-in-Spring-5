package org.rpis5.chapters.chapter_07.r2dbs;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BookRepository
   extends ReactiveCrudRepository<Book, Integer> {

   @Query("SELECT * FROM book WHERE publishing_year = " +
          "(SELECT MAX(publishing_year) FROM book)")
   Flux<Book> findTheLatestBooks();
}

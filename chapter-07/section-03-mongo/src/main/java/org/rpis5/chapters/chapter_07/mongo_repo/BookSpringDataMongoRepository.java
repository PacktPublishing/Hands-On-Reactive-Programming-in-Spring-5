package org.rpis5.chapters.chapter_07.mongo_repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookSpringDataMongoRepository
   extends MongoRepository<Book, Integer> {

   Iterable<Book> findByAuthorsOrderByPublishingYearDesc(String... authors);

   @Query("{ 'authors.1': { $exists: true } }")
   Iterable<Book> booksWithFewAuthors();
}


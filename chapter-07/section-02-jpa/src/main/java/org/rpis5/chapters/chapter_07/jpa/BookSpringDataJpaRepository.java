package org.rpis5.chapters.chapter_07.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookSpringDataJpaRepository
   extends PagingAndSortingRepository<Book, Integer> {

   Iterable<Book> findByIdBetween(int lower, int upper);

   @Query("SELECT b FROM Book b WHERE " +
          "LENGTH(b.title) = (SELECT MIN(LENGTH(b2.title)) FROM Book b2)")
   Iterable<Book> findShortestTitle();
}


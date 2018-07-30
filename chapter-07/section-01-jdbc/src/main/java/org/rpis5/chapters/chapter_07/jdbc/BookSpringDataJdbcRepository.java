/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.jdbc;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookSpringDataJdbcRepository extends CrudRepository<Book, Integer> {

   @Query("SELECT * FROM book " +
          "WHERE LENGTH(title) = (SELECT MAX(LENGTH(title)) FROM book)")
   List<Book> findByLongestTitle();
}

package org.rpis5.chapters.chapter_07.jdbc;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

/**
 * DAO implemented with the Jdbi library
 */
public interface BookJdbiDao {

   @SqlQuery("SELECT * FROM book ORDER BY id DESC")
   @RegisterBeanMapper(Book.class)
   List<Book> listBooks();

   @SqlUpdate("INSERT INTO book(id, title) VALUES (:id, :title)")
   void insertBook(@BindBean Book book);
}

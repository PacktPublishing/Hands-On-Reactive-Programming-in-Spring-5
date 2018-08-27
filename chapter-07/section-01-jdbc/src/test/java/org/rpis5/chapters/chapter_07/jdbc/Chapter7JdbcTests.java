package org.rpis5.chapters.chapter_07.jdbc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Chapter7JdbcTests {
   private static final Logger log = LoggerFactory.getLogger(Chapter7JdbcTests.class);

	@Test
	public void jdbcTest() throws SQLException {
      try(Connection conn = DriverManager.getConnection("jdbc:h2:mem:t1")) {

         conn.createStatement()
            .executeUpdate("CREATE TABLE book (id INTEGER, title VARCHAR(255))");

         try (PreparedStatement insertBook = conn
            .prepareStatement("INSERT INTO book values(?, ?)")) {

            insertBook.setInt(1, 1);
            insertBook.setString(2, "The Martian");
            insertBook.executeUpdate();

            insertBook.setInt(1, 2);
            insertBook.setString(2, "Blue Martian");
            insertBook.executeUpdate();

            log.info("Schema created, data inserted");
         }

         try(ResultSet rows = conn.createStatement()
            .executeQuery("SELECT * FROM book")
         ) {
            log.info("Forward traversal");

            while (rows.next()) {
               log.info("id: {}, title: {}",
                  rows.getInt(1), rows.getString("title"));
            }
         }

         try(ResultSet rows = conn
            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
            .executeQuery("SELECT * FROM book")
         ) {
            log.info("Backward traversal");

            rows.last();
            do {
               log.info("[Reverse] id: {}, title: {}",
                  rows.getInt(1), rows.getString("title"));
            } while (rows.previous());
         }
      } finally {
         log.info("Connection closed");
      }
   }

}

package org.rpis5.chapters.chapter_07.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookJdbcRepository {

   @Autowired
   JdbcTemplate jdbcTemplate;

   public Book findById(int id) {
      return jdbcTemplate.queryForObject(
         "SELECT * FROM book WHERE id=?",
         new Object[] { id },
         new BeanPropertyRowMapper<>(Book.class));
   }

   public List<Book> findByTitle(String phrase) {
      NamedParameterJdbcTemplate named =
         new NamedParameterJdbcTemplate(jdbcTemplate);
      SqlParameterSource namedParameters
         = new MapSqlParameterSource("search_phrase", phrase);

      String sql = "SELECT * FROM book WHERE title = :search_phrase";

      return named.query(
         sql,
         namedParameters,
         new BeanPropertyRowMapper<>(Book.class));
   }

   public List<Book> findAll() {
      return jdbcTemplate.query("SELECT * FROM book", new BookMapper());
   }

   static class BookMapper implements RowMapper<Book> {

      @Override
      public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
         return new Book(rs.getInt("id"), rs.getString("title"));
      }
   }
}

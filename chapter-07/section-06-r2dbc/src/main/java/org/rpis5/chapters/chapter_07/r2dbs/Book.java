package org.rpis5.chapters.chapter_07.r2dbs;

import lombok.Data;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("book")
@Data
public class Book {
   @Id Integer id;
   String title;
   Integer publishingYear;

   public Book(Integer id, String title, Integer publishingYear) {
      this.id = id;
      this.title = title;
      this.publishingYear = publishingYear;
   }

   public Book(String title, Integer publishingYear) {
      this(null, title, publishingYear);
   }

   public Book() {
      this(null, null, null);
   }
}

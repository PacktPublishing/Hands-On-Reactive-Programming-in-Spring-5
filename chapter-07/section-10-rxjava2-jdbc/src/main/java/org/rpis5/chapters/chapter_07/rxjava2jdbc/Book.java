package org.rpis5.chapters.chapter_07.rxjava2jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
   private Integer id;
   private String title;
   private Integer publishingYear;

   public Book(String title, int publishingYear) {
      this(null, title, publishingYear);
   }
}


package org.rpis5.chapters.chapter_07.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
   @Id
   private int id;
   private String title;
}

package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;
import java.util.List;

@Document(collection = "book")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Wither
public class Book {
   @Id
   private ObjectId id;

   @Indexed
   private String title;

   @Field("pubYear")
   private int publishingYear;

   @Indexed
   private List<String> authors;

   public Book(String title, int publishingYear, String... authors) {
      this.title = title;
      this.publishingYear = publishingYear;
      this.authors = Arrays.asList(authors);
   }
}

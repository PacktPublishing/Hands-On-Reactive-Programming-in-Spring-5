package org.rpis5.chapters.chapter_07.webflux;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat-messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
   @JsonIgnore
   @Id private ObjectId id;
   private LocalDateTime time;
   private String user;
   private String messageBody;
}

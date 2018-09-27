package org.rpis5.chapters.chapter_05.reactive_app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = SensorsReadings.COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SensorsReadings {
   public static final String COLLECTION_NAME = "iot-readings";

   @JsonIgnore
   @Id private ObjectId id;

   private LocalDateTime readingTime;

   private Double temperature;
   private Double humidity;
   private Double luminosity;
}

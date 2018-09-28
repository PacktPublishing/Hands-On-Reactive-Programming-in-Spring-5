package org.rpis5.chapters.chapter_08.dataflow.mongodb.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongodbProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongodbProcessorConfiguration.class, args);
    }
}

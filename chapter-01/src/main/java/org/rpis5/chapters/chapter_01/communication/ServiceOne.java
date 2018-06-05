package org.rpis5.chapters.chapter_01.communication;

import org.rpis5.chapters.chapter_01.commons.ExamplesCollection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Example of blocking communication
 */
@RestController
@RequestMapping("api/v1/resource/a")
public class ServiceOne {
    private static final String PORT = "8080";

    @GetMapping
    public ExamplesCollection processRequest() {
        RestTemplate template = new RestTemplate();
        ExamplesCollection result = template.getForObject(
                "http://localhost:" + PORT + "/api/v1/resource/b",
                ExamplesCollection.class
        );

        processResultFurther(result);

        return result;
    }

    private void processResultFurther(ExamplesCollection result) {
        // Do some processing
    }
}

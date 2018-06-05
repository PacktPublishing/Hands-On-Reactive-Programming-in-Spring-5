package org.rpis5.chapters.chapter_01.spring_futures;

import org.rpis5.chapters.chapter_01.commons.ExamplesCollection;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.Future;

/**
 * Example of dirty async communication
 */
@RestController
@RequestMapping("api/v2/resource/a")
public class AsyncServiceOne {
    private static final String PORT = "8080";

    @GetMapping
    public Future<?> process() {
        AsyncRestTemplate template = new AsyncRestTemplate();
        SuccessCallback onSuccess = r -> System.out.println("Success");
        FailureCallback onFailure = e -> System.out.println("Failure");
        ListenableFuture<?> response = template.getForEntity(
                "http://localhost:" + PORT + "/api/v2/resource/b",
                ExamplesCollection.class
        );

        response.addCallback(onSuccess, onFailure);

        return response;
    }
}

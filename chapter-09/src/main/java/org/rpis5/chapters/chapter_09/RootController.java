/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_09;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping("")
    public Flux<String> hello() {
        return Flux.just("Go to http://localhost:8080/payments");
    }
}

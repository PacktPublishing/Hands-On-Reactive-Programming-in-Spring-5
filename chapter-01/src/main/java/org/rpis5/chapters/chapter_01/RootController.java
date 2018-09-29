/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping("")
    public String root() {
        return "Please go to http://localhost:8080/api/v1/resource/a or to http://localhost:8080/api/v2/resource/a";
    }
}

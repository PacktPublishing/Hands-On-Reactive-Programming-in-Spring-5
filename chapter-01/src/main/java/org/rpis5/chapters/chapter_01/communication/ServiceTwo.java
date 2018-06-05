package org.rpis5.chapters.chapter_01.communication;

import org.rpis5.chapters.chapter_01.commons.ExamplesCollection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/resource/b")
public class ServiceTwo {

    @GetMapping
    public ExamplesCollection process() throws InterruptedException {
        Thread.sleep(1000);

        ExamplesCollection ec = new ExamplesCollection();
        ec.setValue("test");

        return ec;
    }
}

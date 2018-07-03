package org.rpis5.chapters.chapter_03.news_service;

import java.util.Collections;

import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;

public class StubNewsLetter extends NewsLetter {

    StubNewsLetter(int element) {
        super(String.valueOf(element), null, Collections.emptyList());
    }
}

package org.rpis5.chapters.chapter_03.news_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Wither;

import java.util.Collection;

@Data(staticConstructor = "of")
@Builder(builderClassName = "NewsLetterTemplate", builderMethodName = "template")
@AllArgsConstructor
@Wither
public class NewsLetter {

    private final @NonNull String title;
    private final          String recipient;
    private final @NonNull Collection<News> digest;
}
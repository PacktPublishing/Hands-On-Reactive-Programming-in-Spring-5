package org.rpis5.chapters.chapter_01.completion_stage;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

import java.util.concurrent.CompletionStage;

public interface ShoppingCardService {
    CompletionStage<Output> calculate(Input value);
}

package org.rpis5.chapters.chapter_01.callbacks;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

import java.util.function.Consumer;

public interface ShoppingCardService {
    void calculate(Input value, Consumer<Output> c);
}

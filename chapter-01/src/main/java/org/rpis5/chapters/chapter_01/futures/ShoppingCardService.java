package org.rpis5.chapters.chapter_01.futures;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

import java.util.concurrent.Future;

public interface ShoppingCardService {
    Future<Output> calculate(Input value);
}

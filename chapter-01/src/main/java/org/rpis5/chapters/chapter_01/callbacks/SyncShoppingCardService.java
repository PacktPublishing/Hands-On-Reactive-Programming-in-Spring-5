package org.rpis5.chapters.chapter_01.callbacks;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

import java.util.function.Consumer;

public class SyncShoppingCardService implements ShoppingCardService {

    @Override
    public void calculate(Input value, Consumer<Output> c) {
        // No blocking operation, better to immediately provide answer
        c.accept(new Output());
    }
}

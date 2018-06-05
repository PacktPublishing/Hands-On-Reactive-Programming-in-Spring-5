package org.rpis5.chapters.chapter_01.imperative;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

public class BlockingShoppingCardService implements ShoppingCardService {

    @Override
    public Output calculate(Input value) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new Output();
    }
}

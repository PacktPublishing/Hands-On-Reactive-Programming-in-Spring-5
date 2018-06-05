package org.rpis5.chapters.chapter_01.callbacks;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

import java.util.function.Consumer;

public class AsyncShoppingCardService implements ShoppingCardService {

    @Override
    public void calculate(Input value, Consumer<Output> c) {
        // blocking operation is presented, better to provide answer asynchronously
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            c.accept(new Output());
        }).start();
    }
}

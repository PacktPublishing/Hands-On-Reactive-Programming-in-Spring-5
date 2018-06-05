package org.rpis5.chapters.chapter_01.imperative;

import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

public class OrdersService {

    private final ShoppingCardService scService;

    public OrdersService(ShoppingCardService scService) {
        this.scService = scService;
    }

    void process() {
        Input input = new Input();
        Output output = scService.calculate(input);

        System.out.println(scService.getClass().getSimpleName() + " execution completed");
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new OrdersService(new BlockingShoppingCardService()).process();
        new OrdersService(new BlockingShoppingCardService()).process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));
    }
}

package org.rpis5.chapters.chapter_01.callbacks;

import org.rpis5.chapters.chapter_01.commons.Input;

public class OrdersService {
    private final ShoppingCardService shoppingCardService;

    public OrdersService(ShoppingCardService shoppingCardService) {
        this.shoppingCardService = shoppingCardService;
    }

    void process() {
        Input input = new Input();
        shoppingCardService.calculate(input, output -> {
            System.out.println(shoppingCardService.getClass().getSimpleName() + " execution completed");
        });
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        OrdersService ordersServiceAsync = new OrdersService(new AsyncShoppingCardService());
        OrdersService ordersServiceSync = new OrdersService(new SyncShoppingCardService());

        ordersServiceAsync.process();
        ordersServiceAsync.process();
        ordersServiceSync.process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));

        Thread.sleep(1000);
    }
}

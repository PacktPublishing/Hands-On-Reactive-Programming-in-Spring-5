package org.rpis5.chapters.chapter_01.completion_stage;

import org.rpis5.chapters.chapter_01.commons.Input;

public class OrdersService {
    private final ShoppingCardService shoppingCardService;

    public OrdersService(ShoppingCardService shoppingCardService) {
        this.shoppingCardService = shoppingCardService;
    }

    void process() {
        Input input = new Input();

        shoppingCardService.calculate(input)
                           .thenAccept(v -> System.out.println(shoppingCardService.getClass().getSimpleName() + " execution completed"));

        System.out.println(shoppingCardService.getClass().getSimpleName() + " calculate called");
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        OrdersService ordersService1 = new OrdersService(new CompletionStageShoppingCardService());

        ordersService1.process();
        ordersService1.process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));

        Thread.sleep(1000);
    }
}

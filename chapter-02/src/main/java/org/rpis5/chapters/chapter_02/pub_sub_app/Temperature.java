package org.rpis5.chapters.chapter_02.pub_sub_app;

/**
 * Temperature in Celsius.
 */
public final class Temperature {
   private final double value;

   public Temperature(double value) {
      this.value = value;
   }

   public double getValue() {
      return value;
   }
}

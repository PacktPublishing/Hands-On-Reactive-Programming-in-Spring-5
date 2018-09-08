package org.rpis5.chapters.chapter_07.rxjava2jdbc.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wallet {
   private Integer id;
   private String owner;
   private int balance;

   // Some statistics
   private int depositOperations;
   private int withdrawOperations;

   public boolean hasEnoughFunds(int amount) {
      return balance >= amount;
   }

   public void withdraw(int amount) {
      if (!hasEnoughFunds(amount)) {
         throw new RuntimeException("Not enough funds!");
      }
      this.balance = this.balance - amount;
      this.withdrawOperations += 1;
   }

   public void deposit(int amount) {
      this.balance = this.balance + amount;
      this.depositOperations += 1;
   }

   public static Wallet wallet(Integer id, String owner, int balance) {
      return new Wallet(id, owner, balance, 0, 0);
   }
}




package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Wither
@Data
@Document(collection = "wallet")
public class Wallet {
   @Id private ObjectId id;
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

   public static Wallet wallet(String owner, int balance) {
      return new Wallet(new ObjectId(), owner, balance, 0, 0);
   }
}




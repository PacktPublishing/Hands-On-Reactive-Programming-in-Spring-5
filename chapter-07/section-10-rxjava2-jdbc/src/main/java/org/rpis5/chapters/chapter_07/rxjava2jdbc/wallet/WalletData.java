package org.rpis5.chapters.chapter_07.rxjava2jdbc.wallet;

import org.davidmoten.rx.jdbc.annotations.Column;
import org.davidmoten.rx.jdbc.annotations.Query;

@Query("select id, owner, balance, deposits, withdraws from wallet")
public interface WalletData {
   @Column Integer id();
   @Column String owner();
   @Column Integer balance();

   // Some statistics
   @Column Integer deposits();
   @Column Integer withdraws();
}

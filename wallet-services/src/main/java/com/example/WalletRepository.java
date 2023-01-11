package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Wallet findByUserName(String userName);

//    @Modifying
//    @Query("select wallet w from wallets set w.amount = w.amount + :amount where w.userName = :userName")
//    int updateWallet(String userName,int amount);
}

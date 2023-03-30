package com.exchanger.repository;

import com.exchanger.entity.CurrencyEnum;
import com.exchanger.entity.User;
import com.exchanger.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByUser(User user);

    Wallet findByUserAndCurrency(User user, CurrencyEnum currency);
}

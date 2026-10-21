package com.WAR.BankAPI.repos;

import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.AccountType;
import com.WAR.BankAPI.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountDAO extends JpaRepository<Account, Long> {

    List<Account> findByUser(User user);

    Optional<Account> findByAccountNumberAndUser(
        Long accountNumber,
        User user
    );

    boolean existsByUserAndAccountType(
        User user,
        AccountType accountType
    );
}
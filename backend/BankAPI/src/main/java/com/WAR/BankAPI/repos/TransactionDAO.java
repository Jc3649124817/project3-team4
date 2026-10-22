package com.WAR.BankAPI.repos;

import com.WAR.BankAPI.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionDAO extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction> {

}

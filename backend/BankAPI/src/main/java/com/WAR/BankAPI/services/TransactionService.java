package com.WAR.BankAPI.services;

import com.WAR.BankAPI.models.Transaction;
import com.WAR.BankAPI.repos.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import static com.WAR.BankAPI.specs.TransactionSpecs.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class TransactionService {
    private final TransactionDAO transactionDAO;

    @Autowired
    public TransactionService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

   public List<Transaction> getTransactions( int numTransactions,
                                            String sortBy, String direction, String type,
                                            BigDecimal minAmount, BigDecimal maxAmount,
                                            Long accountId) {

       List<PredicateSpecification<Transaction>> specs = Stream.of(
               hasType(type),
               amountAtLeast(minAmount),
               amountAtMost(maxAmount),
               involvesAccount(accountId)
       ).filter(Objects::nonNull).toList();

       Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

       return transactionDAO.findBy(
               PredicateSpecification.allOf(specs),
               q -> q.sortBy(sort).limit(numTransactions).all()
       );

   }



}

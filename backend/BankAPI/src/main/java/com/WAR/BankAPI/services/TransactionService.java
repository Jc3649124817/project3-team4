package com.WAR.BankAPI.services;

import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.Transaction;
import com.WAR.BankAPI.DTOs.TransactionDTO;
import com.WAR.BankAPI.models.TransactionType;
import com.WAR.BankAPI.repos.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import static com.WAR.BankAPI.specs.TransactionSpecs.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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

   public List<TransactionDTO> getTransactions( int numTransactions,
                                            String sortBy, String direction, String type,
                                            BigDecimal minAmount, BigDecimal maxAmount,
                                            Long accountId) {

       if(type != null && !TransactionType.contains(type)){
           throw new InvalidRequestException("Invalid transaction type entered.");
       }
       if(minAmount != null && minAmount.compareTo(BigDecimal.ZERO) <= 0){
           throw new InvalidRequestException("Min amount must be greater than 0.");
       }
       if(maxAmount != null && maxAmount.compareTo(BigDecimal.ZERO) <= 0){
           throw new InvalidRequestException("Max amount must be greater than 0.");
       }
       if(minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0){
           throw new InvalidRequestException("Min amount must be equal or lower than max amount.");
       }
       //TODO: Check that optional accountId belongs to the logged in user.

        if(type != null && !TransactionType.contains(type)){
            type = null;
        }

       List<PredicateSpecification<Transaction>> specs = Stream.of(
               hasType(type),
               amountAtLeast(minAmount),
               amountAtMost(maxAmount),
               involvesAccount(accountId)
       ).filter(Objects::nonNull).toList();



       Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

       List<Transaction> transactions = transactionDAO.findBy(
               PredicateSpecification.allOf(specs),
               q -> q.sortBy(sort).limit(numTransactions).all());

        List<TransactionDTO> transactionDTOs = new ArrayList<>();
        for(Transaction t: transactions){
            transactionDTOs.add(TransactionDTO.from(t));
       }
        return transactionDTOs;
   }



}

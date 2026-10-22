package com.WAR.BankAPI.models;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = true)
    private Long destinationId;
    @Column(nullable = true)
    private Long originId;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userId")
    private User user;


    public Transaction() {
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public Long getOriginId() {
        return originId;
    }

    public User getUser() {
        return user;
    }

    public Instant getDate() {
        return date;
    }



    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", type=" + transactionType +
                ", amount=" + amount +
                ", destinationId='" + destinationId + '\'' +
                ", originId='" + originId + '\'' +
                ", user=" + user +
                '}';
    }
}

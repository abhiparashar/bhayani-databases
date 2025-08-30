package com.example.demo.prac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String transactionId;
    private TransactionState transactionState;
    private long startTimeStamp;

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
        this.transactionState = TransactionState.ACTIVE;
        this.startTimeStamp = System.currentTimeMillis();
    }
}

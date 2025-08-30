package com.example.demo.dblearning;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private String transactionId;
    private TransactionState state;
    private long startTimestamp;

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
        this.state = TransactionState.ACTIVE;
        this.startTimestamp = System.currentTimeMillis();
    }
}

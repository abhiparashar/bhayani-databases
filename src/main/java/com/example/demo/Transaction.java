package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Transaction {
    private final String transactionId;
    private final TransactionState state;
    private long startTimestamp;

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
        this.state = TransactionState.ACTIVE;
        this.startTimestamp = System.currentTimeMillis();
    }
}

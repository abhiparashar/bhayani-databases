package com.example.demo.prac;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionIsolationManager {
    // Component 1: Data Storage
    // Main database storage
    private final ConcurrentHashMap<String, Object>mainDatabase = new ConcurrentHashMap<>();

    // Latest committed values
    private final ConcurrentHashMap<String, Object>committedData = new ConcurrentHashMap<>();

    // Per-transaction uncommitted changes: txnId -> {accountId -> value}
    private final ConcurrentHashMap<String, ConcurrentMap<String, Object>>uncommittedData = new ConcurrentHashMap<>();

    // Per-transaction snapshots: txnId -> {accountId -> value}
    private final ConcurrentHashMap<String,ConcurrentMap<String, Object>>snapshots = new ConcurrentHashMap<>();

    // Component 2: Transaction Management
    // Add active transaction tracking
    private final ConcurrentHashMap<String,Transaction>activeTransactions = new ConcurrentHashMap<>();

    // Component 3: Lock Management
    // Add locks for SERIALIZABLE isolation
    private final ConcurrentHashMap<String, ReentrantLock>recordLocks = new ConcurrentHashMap<>();

    // Constructor - Initialize with sample data
    public TransactionIsolationManager() {
        initializeSampleData();
    }

    private void initializeSampleData(){
        // Initialize both mainDatabase and committedData with same values
        mainDatabase.put("account_123", 1000.0);
        mainDatabase.put("account_456", 2000.0);
        mainDatabase.put("account_789", 1500.0);

        committedData.put("account_123", 1000.0);
        committedData.put("account_456", 2000.0);
        committedData.put("account_789", 1500.0);

        System.out.println("Initialized sample data: " + mainDatabase);
    }

    // Component 4: Core Operations
    /**
     * Begin a new transaction
     */
    public Transaction beginTransaction(String transactionId){
        Transaction transaction = new Transaction(transactionId);
        activeTransactions.put(transactionId,transaction);
        System.out.println("Started transaction: " + transactionId);
        return transaction;
    }

}

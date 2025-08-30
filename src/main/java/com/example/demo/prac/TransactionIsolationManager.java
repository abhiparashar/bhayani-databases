package com.example.demo.prac;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TransactionIsolationManager {
    // Component 1: Data Storage
    // Main database storage
    private ConcurrentHashMap<String, Object>mainDatabase = new ConcurrentHashMap<>();

    // Latest committed values
    private ConcurrentHashMap<String, Object>committedData = new ConcurrentHashMap<>();

    // Per-transaction uncommitted changes: txnId -> {accountId -> value}
    private ConcurrentHashMap<String, ConcurrentMap<String, Object>>uncommittedData = new ConcurrentHashMap<>();

    // Per-transaction snapshots: txnId -> {accountId -> value}
    private ConcurrentHashMap<String,ConcurrentMap<String, Object>>snapshots = new ConcurrentHashMap<>();
}

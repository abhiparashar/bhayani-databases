package com.example.demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionIsolationManager {

    // Main database storage
    private final ConcurrentHashMap<String, Object> mainDatabase = new ConcurrentHashMap<>();

    // Latest committed values
    private final ConcurrentHashMap<String, Object> committedData = new ConcurrentHashMap<>();

    // Per-transaction uncommitted changes: txnId -> {accountId -> value}
    private final ConcurrentHashMap<String, Map<String, Object>> uncommittedData = new ConcurrentHashMap<>();

    // Per-transaction snapshots: txnId -> {accountId -> value}
    private final ConcurrentHashMap<String, Map<String, Object>> snapshots = new ConcurrentHashMap<>();

}
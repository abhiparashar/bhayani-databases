package com.example.demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionIsolationManager {

    // Main database storage
    private final ConcurrentHashMap<String, Object> mainDatabase = new ConcurrentHashMap<>();

    // Latest committed values
    private final ConcurrentHashMap<String, Object> committedData = new ConcurrentHashMap<>();

    // Per-transaction uncommitted changes: txnId -> {accountId -> value}
    private final ConcurrentHashMap<String, Map<String, Object>> uncommittedData = new ConcurrentHashMap<>();

    // Per-transaction snapshots: txnId -> {accountId -> value}
    private final ConcurrentHashMap<String, Map<String, Object>> snapshots = new ConcurrentHashMap<>();

    //add locks
    private final ConcurrentHashMap<String, ReentrantLock>recordLocks = new ConcurrentHashMap<>();

    // Add active transaction tracking:
    private final ConcurrentHashMap<String, Transaction> activeTransactions = new ConcurrentHashMap<>();

    public Transaction beginTransaction(String transactionId) {
        Transaction transaction = new Transaction(transactionId);
        activeTransactions.put(transactionId, transaction);
        return transaction;
    }

    public Object read(String transactionId, String key, IsolationLevel level) {
        switch (level) {
            case READ_UNCOMMITTED:
                // Check own uncommitted writes first
                Map<String, Object> txUncommitted = uncommittedData.get(transactionId);
                if (txUncommitted != null && txUncommitted.containsKey(key)) {
                    return txUncommitted.get(key);  // Return own write
                }
                // Otherwise read from mainDatabase
                return mainDatabase.get(key);

            case READ_COMMITTED:
                // Check own uncommitted writes first
                Map<String, Object> txUncommittedCommitted = uncommittedData.get(transactionId);
                if (txUncommittedCommitted != null && txUncommittedCommitted.containsKey(key)) {
                    return txUncommittedCommitted.get(key);  // Return own write
                }
                // Otherwise read from committed data only
                return committedData.get(key);

            case REPEATABLE_READ:
                // Get or create snapshot for this transaction
                Map<String, Object> snapshot = snapshots.computeIfAbsent(transactionId,
                        k -> createSnapshot(transactionId));
                return snapshot.get(key);

            case SERIALIZABLE:
                // Acquire lock first
                ReentrantLock lock = recordLocks.computeIfAbsent(key,
                        k -> new ReentrantLock());
                lock.lock();
                try {
                    // Same as REPEATABLE_READ but with locking
                    Map<String, Object> snapshotSerial = snapshots.computeIfAbsent(transactionId,
                            k -> createSnapshot(transactionId));
                    return snapshotSerial.get(key);
                } finally {
                    lock.unlock();
                }
        }
        return null;
    }

    // Helper method for snapshot creation
    private Map<String, Object> createSnapshot(String transactionId) {
        Transaction tx = activeTransactions.get(transactionId);
        Map<String, Object> snapshot = new ConcurrentHashMap<>();

        // Copy committed data that was committed before this transaction started
        for (Map.Entry<String, Object> entry : committedData.entrySet()) {
            // In real implementation, you'd filter by timestamp
            // For now, just copy all committed data
            snapshot.put(entry.getKey(), entry.getValue());
        }

        return snapshot;
    }

    public void write(String transactionId, String key, Object value, IsolationLevel level) {
        // Get or create uncommitted map for this transaction
        Map<String, Object> txUncommitted = uncommittedData.computeIfAbsent(transactionId,
                k -> new ConcurrentHashMap<>());

        if (level == IsolationLevel.SERIALIZABLE) {
            // Acquire exclusive lock
            ReentrantLock lock = recordLocks.computeIfAbsent(key,
                    k -> new ReentrantLock());
            lock.lock();
            try {
                txUncommitted.put(key, value);
            } finally {
                lock.unlock();
            }
        } else {
            // For other isolation levels, just write to uncommitted
            txUncommitted.put(key, value);
        }
    }

    public void commit(String transactionId) {
        // Get transaction's uncommitted changes
        Map<String, Object> txUncommitted = uncommittedData.get(transactionId);

        if (txUncommitted != null) {
            // Move all changes to committed data
            for (Map.Entry<String, Object> entry : txUncommitted.entrySet()) {
                committedData.put(entry.getKey(), entry.getValue());
                mainDatabase.put(entry.getKey(), entry.getValue()); // Update main DB too
            }

            // Clean up uncommitted data
            uncommittedData.remove(transactionId);
        }

        // Clean up snapshot
        snapshots.remove(transactionId);

        // Update transaction state and remove from active
        Transaction tx = activeTransactions.get(transactionId);
        if (tx != null) {
            // Note: Can't change state since it's final - this is a design issue we'll fix later
            activeTransactions.remove(transactionId);
        }
    }

    public void rollback(String transactionId) {
        // Simply remove uncommitted changes (discard them)
        uncommittedData.remove(transactionId);

        // Clean up snapshot
        snapshots.remove(transactionId);

        // Remove from active transactions
        activeTransactions.remove(transactionId);

        // Note: In real implementation, you'd also update transaction state
    }

}
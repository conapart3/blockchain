package com.conal.blockchain.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionPoolTest
{
    private Transaction transaction;
    private Wallet wallet;
    private TransactionPool transactionPool;

    @BeforeEach
    void setUp()
    {
        wallet = new Wallet(2000);
        transaction = new Transaction(wallet, "recipient", 1234);
        transactionPool = new TransactionPool();
    }

    @Test
    void testTransactionAddedToPool()
    {
        transactionPool.setTransaction(transaction);

        assertTrue(transactionPool.getTransactionMap().containsKey(transaction.getId()));
    }
}
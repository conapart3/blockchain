package com.conal.blockchain.core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TransactionPool
{
    // collect transactions from various contributors over time.
    // using a map to uniquely hold
    // todo - should this transactionMap hold the WalletAddress - Transaction mapping?
    private Map<String, Transaction> transactionMap;

    public TransactionPool()
    {
        this.transactionMap = new HashMap<>();
    }

    public void setMap(Map<String, Transaction> transactionMap)
    {
        this.transactionMap = transactionMap;
    }

    public void setTransaction(Transaction transaction)
    {
        if (transactionMap.containsKey(transaction.getId()))
        {
            log.info("Overwriting transaction with ID {}. Before: {}, After: {}",
                    transaction.getId(),
                    transactionMap.get(transaction.getId()),
                    transaction);
        }
        else
        {
            log.info("Setting transaction with ID {} to transaction pool.", transaction.getId());
        }
        transactionMap.put(transaction.getId(), transaction);
    }

    public Map<String, Transaction> getTransactionMap()
    {
        return transactionMap;
    }

    public Transaction findExistingTransactionForWalletAddress(String walletAddress)
    {
        Optional<Transaction> transactionPublicKeyExists = transactionMap.values().stream()
                .filter(transaction -> transaction.getOutputMap().containsKey(walletAddress))
                .findFirst();

        return transactionPublicKeyExists.orElse(null);
    }

    public List<Transaction> getValidTransactions()
    {
        return transactionMap.values().stream()
                .filter(transaction -> Transaction.validTransaction(transaction))
                .collect(Collectors.toList());
    }
}

package com.conal.blockchain.core;

import com.conal.blockchain.redis.MessagePublisher;

import java.util.List;

public class TransactionMiner
{
    private Blockchain blockchain;
    private TransactionPool transactionPool;
    private Wallet wallet;
    private MessagePublisher mainTopicPublisher;

    public TransactionMiner(Blockchain blockchain, TransactionPool transactionPool, Wallet wallet, MessagePublisher mainTopicPublisher)
    {
        this.blockchain = blockchain;
        this.transactionPool = transactionPool;
        this.wallet = wallet;
        this.mainTopicPublisher = mainTopicPublisher;
    }

    public void mineTransactions()
    {
        // coordinate quite a few behaviours
        // get from the transaction pool
        // but not all transactions!
        // we only get validated transactions
        // get the transaction pool's valid transactions
        List<Transaction> transactions = transactionPool.getValidTransactions();

        // generate reward for the miner


        // add a block consisting of these transactions to the blockchain
        blockchain.mineBlock(transactions.toString());

        // broadcast the updated blockchain
        //todo

        // clear the transaction pool
        //todo
    }
}

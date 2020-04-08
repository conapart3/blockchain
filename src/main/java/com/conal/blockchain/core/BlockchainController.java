package com.conal.blockchain.core;

import com.conal.blockchain.api.BlockchainApiClient;
import com.conal.blockchain.redis.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Slf4j
public class BlockchainController
{
    @Value("${server.port}")
    private int serverPort;

    @Value("${rootnode.server.port}")
    private int rootNodeServerPort;

    private TransactionPool transactionPool;
    private final Wallet wallet;
    private Blockchain blockchain;
    private MessagePublisher mainTopicPublisher;
    private MessagePublisher transactionTopicPublisher;

    public BlockchainController(MessagePublisher mainTopicPublisher,
                                MessagePublisher transactionTopicPublisher)
    {
        this.mainTopicPublisher = mainTopicPublisher;
        this.transactionTopicPublisher = transactionTopicPublisher;
        this.wallet = new Wallet(1000);

        updateBlockchainFromRootNode();
        updateTransactionsFromRootNode();
    }

    private void updateBlockchainFromRootNode()
    {
        if (serverPort == rootNodeServerPort)
        {
            // in the case of the rootNode there are no updates to be obtained, create new blockchain
            createBlockchain();
            return;
        }

        BlockchainApiClient blockchainApiClient = new BlockchainApiClient();
        List<Block> blocks = blockchainApiClient.queryRootNodeBlocks(rootNodeServerPort);
        blockchain.replaceChain(blocks);
    }

    private void updateTransactionsFromRootNode()
    {
        if (serverPort == rootNodeServerPort)
        {
            // in the case of the rootNode just create empty TransactionPool
            this.transactionPool = new TransactionPool();
            return;
        }

        BlockchainApiClient blockchainApiClient = new BlockchainApiClient();
        TransactionPool transactionPool = blockchainApiClient.queryRootNodeTransactionPool(rootNodeServerPort);
        this.transactionPool = transactionPool;
    }

    public void createBlockchain()
    {
        blockchain = new Blockchain();
    }

    public void onBlockchainReceived(List<Block> blocks)
    {
        if (Blockchain.isValidChain(blocks))
        {
            log.info("Replacing chain or size {} with new chain of size {}.", blockchain.getBlocks().size(), blocks.size());
            blockchain.replaceChain(blocks);
        }
    }

    public void onTransactionReceived(Transaction transaction)
    {
        log.info("Transaction received: {}", transaction);
        // no need to update any transactions - this transaction came from a foreign wallet, we just overwrite any existing
        // with same transaction id.
        transactionPool.setTransaction(transaction);
    }

    public void mineBlock(String data)
    {
        blockchain.mineBlock(data);
        mainTopicPublisher.publish(blockchain.getBlocks());
    }

    public List<Block> getBlockChain()
    {
        return blockchain.getBlocks();
    }

    public Transaction addOrUpdateTransaction(final String recipient, final double amount)
    {
        Transaction transaction = createOrUpdateTransaction(recipient, amount);
        transactionTopicPublisher.publish(transaction);
        return transaction;
    }

    public Map<String, Transaction> getTransactionPool()
    {
        return transactionPool.getTransactionMap();
    }

    private Transaction createOrUpdateTransaction(final String recipient, final double amount)
    {
        // todo optional
        Transaction existingTransaction = transactionPool.findExistingTransactionForWalletAddress(wallet.getAddress());
        if (existingTransaction != null)
        {
            // the wallet creates / updates transactions.
            wallet.updateTransaction(existingTransaction, recipient, amount);
            return existingTransaction;
        }
        else
        {
            Transaction newTransaction = wallet.createTransaction(recipient, amount);
            transactionPool.setTransaction(newTransaction);
            return newTransaction;
        }
    }
}


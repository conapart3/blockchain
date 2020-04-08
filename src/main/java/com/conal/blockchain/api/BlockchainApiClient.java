package com.conal.blockchain.api;

import com.conal.blockchain.core.Block;
import com.conal.blockchain.core.TransactionPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BlockchainApiClient
{
    private RestTemplate restTemplate;

    public BlockchainApiClient()
    {
        this.restTemplate = new RestTemplate();
    }

    public List<Block> queryRootNodeBlocks(int rootNodeServerPort)
    {
        String resourceUrl = "http://localhost:" + rootNodeServerPort + "/api/blocks";
        Block[] blocksArray = restTemplate.getForObject(resourceUrl, Block[].class);
//        return Arrays.asList(blocks); // this returns a non resizeable list which won't work.
        ArrayList<Block> blocks = new ArrayList<>();
        Collections.addAll(blocks, blocksArray);
        return blocks;
    }

    public TransactionPool queryRootNodeTransactionPool(int rootNodeServerPort)
    {
        String resourceUrl = "http://localhost:" + rootNodeServerPort + "/api/transactionPool";
        return restTemplate.getForObject(resourceUrl, TransactionPool.class);
    }
}

package com.conal.blockchain.redis;

import com.conal.blockchain.core.Block;
import com.conal.blockchain.core.BlockchainController;
import com.conal.blockchain.core.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class RedisMessageSubscriber implements MessageListener
{
    private BlockchainController blockchainController;
    private ObjectMapper objectMapper;

    public RedisMessageSubscriber(BlockchainController blockchainController)
    {
        this.blockchainController = blockchainController;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onMessage(Message message, byte[] pattern)
    {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        log.info("Message received on subscription topic {}: {}", channel, body);

        switch (channel)
        {
            case "BLOCKCHAIN"://todo enum
                try
                {
                    List<Block> blocks = objectMapper.readValue(body,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Block.class));
                    blockchainController.onBlockchainReceived(blocks); // todo
                }
                catch (JsonProcessingException e)
                {
                    throw new RuntimeException(e);
                }
                break;
            case "TRANSACTION":
                try
                {
                    Transaction transaction = objectMapper.readValue(body, Transaction.class);
                    blockchainController.onTransactionReceived(transaction);
                }
                catch (JsonProcessingException e)
                {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }
}

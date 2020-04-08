package com.conal.blockchain;

import com.conal.blockchain.core.Blockchain;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
class BlockchainApplicationTests
{

    @Test
    void contextLoads()
    {
    }

    @Test
    void blockchain()
    {
        Blockchain blockchain = new Blockchain();
        long totalDuration = 0;
        for (int i = 1; i < 1000; i++)
        {
            Instant before = Instant.now();
            blockchain.mineBlock("data");
            Instant after = Instant.now();

            long duration = after.toEpochMilli() - before.toEpochMilli();
            totalDuration = totalDuration + duration;

            System.out.println("Block "
                    + i
                    + ", difficulty: "
                    + blockchain.getBlocks().get(i).getDifficulty()
                    + ", time taken = "
                    + duration
                    + ", average = "
                    + totalDuration / i);
        }
        System.out.println(blockchain);
    }

}

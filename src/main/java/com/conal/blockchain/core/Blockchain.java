package com.conal.blockchain.core;


import com.conal.blockchain.exception.InvalidChainException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Blockchain
{
    private List<Block> blocks;
    // target time in ms for a block to be mined.
    private static final long mineRateMs = 1000;
    private static final int INITIAL_DIFFICULTY = 1;

    public List<Block> getBlocks()
    {
        return blocks;
    }

    public long getMineRate()
    {
        return mineRateMs;
    }

    public Blockchain()
    {
        blocks = new ArrayList<>();
        blocks.add(Block.genesis(INITIAL_DIFFICULTY));
    }

    public void mineBlock(String data)
    {
        Block lastBlock = blocks.get(blocks.size() - 1);
        blocks.add(Block.mineBlock(lastBlock, data, mineRateMs));
    }

    public static boolean isValidChain(List<Block> chain)
    {
        try
        {
            validateChainStartsWithGenesisBlock(chain);
            validateChainOfHashes(chain);
            validateFieldsProduceTheCorrectHash(chain);
            // we also need to validate that the jump in difficulty between blocks is not more than 1
            validateDifficultyJumps(chain);
        }
        catch (InvalidChainException e)
        {
            // todo get rid of exception driven logic?
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private static void validateDifficultyJumps(List<Block> chain) throws InvalidChainException
    {
        for (int i = 1; i < chain.size(); i++)
        {
            if (Math.abs(chain.get(i).getDifficulty() - chain.get(i - 1).getDifficulty()) > 1)
            {
                throw new InvalidChainException(
                        "Difficulty jump of {0} detected between block {1} and block {2}.",
                        Integer.toString(Math.abs(chain.get(i).getDifficulty() - chain.get(i - 1).getDifficulty())),
                        chain.get(i).getHash(),
                        chain.get(i - 1).getHash());
            }
        }
    }

    public void replaceChain(List<Block> newChain)
    {
        if (newChain.size() <= blocks.size())
        {
            log.info("Blockchain not replaced, newChain is not longer.");
            return;
        }
        if (!Blockchain.isValidChain(newChain))
        {
            return;
        }
        log.info("Replacing chain of size {} with new chain of size {}.", blocks.size(), newChain.size());
        this.blocks = newChain;
    }

    private static void validateChainOfHashes(List<Block> blockChain) throws InvalidChainException
    {
        for (int i = 1; i < blockChain.size(); i++)
        {
            Block currentBlock = blockChain.get(i);
            Block previousBlock = blockChain.get(i - 1);
            // add check that the hash is valid (length, not empty, etc)
            if (!currentBlock.getLastHash().equals(previousBlock.getHash()))
            {
                throw new InvalidChainException(
                        "Chain of hashes not valid, block with hash {0} with lastHash {1} does not match previous block's hash {2}",
                        currentBlock.getHash(), currentBlock.getLastHash(), previousBlock.getHash());
            }
        }
    }

    private static void validateFieldsProduceTheCorrectHash(List<Block> blockChain) throws InvalidChainException
    {
        for (int i = 1; i < blockChain.size(); i++)
        {
            Block block = blockChain.get(i);
            Block lastBlock = blockChain.get(i - 1);

            String hash = Block.cryptoHash(
                    block.getLastHash(),
                    block.getData(),
                    block.getCreateTime(),
                    block.getNonce(),
                    lastBlock.getDifficulty());
            if (!hash.equals(block.getHash()))
            {
                throw new InvalidChainException(
                        "Chain not valid, block with hash {0} does not match expected hash for its fields.",
                        block.getHash());
            }
        }
    }

    private static void validateChainStartsWithGenesisBlock(List<Block> blockChain) throws InvalidChainException
    {
        if (!Block.GENESIS_DATA.equals(blockChain.get(0).getHash()))
        {
            throw new InvalidChainException("Chain not valid, does not start with Genesis block.");
        }
    }

    @Override
    public String toString()
    {
        return "Blockchain{" +
                "blocks=" + blocks +
                '}';
    }
}


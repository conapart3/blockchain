package com.conal.blockchain.core;

import com.conal.blockchain.crypto.CryptoUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
@Getter
@Setter(PRIVATE)
@ToString
public class Block
{
    static final String GENESIS_DATA = "GENESIS";
    static final String GENESIS_LASTHASH = "-----";

    // represents a list of transactions or any data
    private String data;
    // hash of previous block
    private String lastHash;
    // this block's hash
    private String hash;
    // timestamp of block's creation time
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant createTime;

    // added difficulty and nonce for evolving block mining difficulty
    private int difficulty;
    private long nonce;

    private Block(Instant date, String data, String lastHash, String hash,
                  int difficulty, long nonce)
    {
        this.createTime = date;
        this.data = data;
        this.lastHash = lastHash;
        this.hash = hash;
        this.difficulty = difficulty;
        this.nonce = nonce;
    }

    public static Block genesis(int initialDifficulty)
    {
        log.info("Welcome to ConalCoin, creating Genesis block.");
        return new Block(
                Instant.now(),
                null,
                GENESIS_LASTHASH,
                GENESIS_DATA,
                initialDifficulty,
                0);
    }

    // creating a block requires some computational work hence the verb 'mine'
    public static Block mineBlock(Block lastBlock, String data, long mineRate)
    {
        int lastBlockDifficulty = lastBlock.getDifficulty();
        long nonce = 0;
        String hash = "";
        Instant nonceFoundTime;
        Instant nonceSearchStartTime = Instant.now();

        final String expectedBeginningOfHash = "0".repeat(lastBlockDifficulty); // repeat is a java 11 method

        log.info("Mining block with difficulty " + lastBlockDifficulty + ", lastBlock: " + lastBlock);
        do
        {
            // we want the timestamp to be of when the nonce was found, not when the miner starts looking for the nonce.
            nonceFoundTime = Instant.now();

            hash = cryptoHash(
                    lastBlock.getHash(),
                    data,
                    nonceFoundTime,
                    nonce,
                    lastBlockDifficulty);

            nonce++;
        }
        while (!convertToBinary(hash).startsWith(expectedBeginningOfHash));


        log.info("Nonce "
                + nonce
                + " found in "
                + (nonceFoundTime.toEpochMilli() - nonceSearchStartTime.toEpochMilli())
                + " ms.");

        int newBlockDifficulty = adjustDifficulty(lastBlock, mineRate, lastBlockDifficulty, nonceFoundTime);

        log.info("Difficulty adjusted from "
                + lastBlockDifficulty
                + " to "
                + newBlockDifficulty);

        return new Block(
                nonceFoundTime,
                data,
                lastBlock.getHash(),
                hash,
                newBlockDifficulty,//this should really be called 'nextBlockDifficulty'
                nonce);
    }

    private static String convertToBinary(String hash)
    {
        byte[] digest = hash.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest)
        {
            sb.append(String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0'));
        }
        return sb.toString();
    }

    private static int adjustDifficulty(Block lastBlock, long mineRate, int difficulty, Instant timeNonceFound)
    {
        if (timeNonceFound.toEpochMilli() - lastBlock.getCreateTime().toEpochMilli() < mineRate)
        {
            // our SHA-256 hash has 256 bits. Difficulty can't go over the max number of leading characters available.
            int maxSizeOfHash = 256;
            if (difficulty < maxSizeOfHash)
            {
                return difficulty + 1;
            }
        }
        else if (timeNonceFound.toEpochMilli() - lastBlock.getCreateTime().toEpochMilli() > mineRate)
        {
            if (difficulty > 1)
            {
                return difficulty - 1;
            }
        }
        return difficulty;
    }

    public static String cryptoHash(String lastBlockHash, String data, Instant createTime, long nonce, int lastBlockDifficulty)
    {
        return CryptoUtil.cryptoHash(lastBlockHash, data, createTime.toString(), Long.toString(nonce), Integer.toString(lastBlockDifficulty));
    }

}

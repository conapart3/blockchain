package com.conal.blockchain.core;

import com.conal.blockchain.crypto.CryptoUtil;
import com.conal.blockchain.exception.BlockchainRuntimeException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.PublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
public class Transaction
{
    private String id;
    private Map<String, Double> outputMap;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant createTime;
    private TransactionInput input;

    // todo change to static factory method
    public Transaction(Wallet senderWallet, String recipient, double amount)
    {
        this.createTime = Instant.now();
        this.id = UUID.randomUUID().toString();
        this.outputMap = createOutputMap(senderWallet, recipient, amount);
        this.input = createInput(senderWallet, createTime, outputMap);
        log.info("Created transaction " + this);
    }

    public static Transaction createRewardTransaction(String recipient, double amount)
    {
        Transaction transaction = new Transaction();
        transaction.createTime = Instant.now();
        transaction.id = UUID.randomUUID().toString();

        transaction.outputMap = new HashMap<>();
        transaction.outputMap.put("*authorized-reward*", 50d);
        transaction.outputMap.put(recipient, 50d);

        transaction.input = new TransactionInput(
                amount,
                transaction.createTime,
                null,
                null);
        log.info("Created reward transaction " + transaction);
        return transaction;
    }

    // todo test
    public static boolean validTransaction(Transaction transaction)
    {
        // validate the amount = input.balance - output.get(senderAddress).getBalance
        Optional<Double> optionalTotalAmount = transaction.outputMap.values()
                .stream()
                .reduce((a, b) -> a + b);

        if (optionalTotalAmount.isEmpty())
        {
            return false;
        }

        if (optionalTotalAmount.get() != transaction.input.balance)
        {
            return false;
        }

        if (!CryptoUtil.verifySignature(
                transaction.input.signature,
                transaction.input.publicKey,
                transaction.outputMap.toString()))
        {
            return false;
        }

        // valid if none of the fields have been tampered with

        // invalid if a transaction output map value is invalid
        // or if the input signature has been faked
        return true;
    }

    private TransactionInput createInput(Wallet senderWallet, Instant createTime, Map<String, Double> outputMap)
    {
        // sender wallet must sign the outputMap and provide its public key.
        return new TransactionInput(
                senderWallet.getBalance(),
                createTime,
                senderWallet.sign(outputMap.toString()),
                senderWallet.getPublicKey());
    }

    private Map<String, Double> createOutputMap(Wallet senderWallet, String recipient, double amount)
    {
        // changes to the wallets of sender and recipient
        // output map contains the address of the recipient and the amount to increment recipient balance by, and the
        // amount the new balance of the senderWallet
        outputMap = new HashMap<>();
        outputMap.put(recipient, amount);
        outputMap.put(senderWallet.getAddress(), senderWallet.getBalance() - amount);
        return outputMap;
    }

    // any wallet can only have one transaction at a time pending to be mined. Hence why we update any existing transaction in the transaction pool!
    public void updateTransaction(Wallet senderWallet, String recipient, double amount)
    {
        Instant updateTime = Instant.now();

        if (StringUtils.isEmpty(recipient))
        {
            throw new BlockchainRuntimeException("Recipient is empty, cannot create Transaction with empty recipient.");
        }

        // validate the senderWallet has enough balance to send this amount!
        if (senderWallet.getBalance() < amount)
        {
            throw new BlockchainRuntimeException("Sender wallet {0} contains only {1} balance to send {2} amount.",
                    senderWallet.getAddress(), Float.toString(senderWallet.getBalance()), Double.toString(amount));
        }

        if (!outputMap.containsKey(recipient))
        {
            throw new BlockchainRuntimeException("Existing transaction does not contain the recipient {0}", recipient);
        }

        log.info("Updating transaction with ID {} with amount {}", id, amount);

        // change the amount to be sent to the recipient
        outputMap.compute(recipient, (k, v) -> v + amount);

        // change the amount sent from the sender
//        outputMap.compute(senderWallet.getAddress(),
//                (k, v) -> senderWallet.getBalance() - amount);

        outputMap.put(senderWallet.getAddress(), senderWallet.getBalance() - amount);

        input = createInput(senderWallet, updateTime, outputMap);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PUBLIC)//todo
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ToString
    static class TransactionInput
    {
        // sender wallet's original balance
        // create time
        // signature of the outputMap,
        // sender address
        private double balance;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        private Instant createTime;
        private byte[] signature;
        @JsonIgnore
        private PublicKey publicKey;
    }
}

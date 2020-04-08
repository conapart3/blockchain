package com.conal.blockchain.core;

import com.conal.blockchain.crypto.CryptoUtil;
import com.conal.blockchain.exception.BlockchainRuntimeException;

import java.security.KeyPair;
import java.security.PublicKey;

/*
3 responsibilities:
- an address to which balance can be sent (publicKey)
- a means to hold a balance, track the balance, calculate the balance by examining the history of the blockchain
- ability to sign data - conduct cryptographically secure transactions by producing signatures
 */
public class Wallet
{
    // we won't have separate fields for these? the private and public keys are a cryptographic pair not just any random keys
//    private String publicKey;// doubles up as address

    private KeyPair keyPair;
    private float balance = 1000; // lets just give everybody a starting balance of 1000?

    public Wallet(float balance)
    {
        this.keyPair = CryptoUtil.generateKey();
        // we want the public key to act as an address so if we can get the hex format of the key that would be great.
//        keyPair.getPublic();
//        char[] c;
//        Hex.decodeHex(c);
        this.balance = balance;
    }

    public byte[] sign(String data)
    {
        return CryptoUtil.generateSignature(
                CryptoUtil.cryptoHash(data),
                keyPair);
    }

    public boolean verifySignature(byte[] signature, String data)
    {
        return CryptoUtil.verifySignature(
                signature,
                keyPair.getPublic(),
                CryptoUtil.cryptoHash(data));
    }

    public Transaction createTransaction(String recipient, double amount)
    {
        if (amount > balance)
        {
            throw new BlockchainRuntimeException("Amount {0} exceeds balance {1}",
                    Double.toString(amount), Float.toString(balance));
        }
        return new Transaction(this, recipient, amount);
    }

    public Transaction updateTransaction(Transaction transaction, String recipient, double newAmount)
    {
        transaction.updateTransaction(this, recipient, newAmount);
        return transaction;
    }

    public PublicKey getPublicKey()
    {
        return keyPair.getPublic();
    }

    public String getAddress()
    {
        return keyPair.getPublic().toString();
    }

    public float getBalance()
    {
        return balance;
    }
}

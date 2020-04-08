package com.conal.blockchain.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest
{
    private Wallet senderWallet;

    @BeforeEach
    void setUp()
    {
        senderWallet = new Wallet(55);
    }

    @Test
    void testCreateTransaction()
    {
        Transaction transaction = new Transaction(senderWallet, "abc", 50);

        assertNotNull(transaction.getId());
        assertNotNull(transaction.getInput());
        assertNotNull(transaction.getOutputMap());
        assertNotNull(transaction.getCreateTime());

        assertEquals(2, transaction.getOutputMap().size());
        assertTrue(transaction.getOutputMap().containsKey("abc"));
        assertTrue(transaction.getOutputMap().containsKey(senderWallet.getAddress()));
        assertEquals(5, transaction.getOutputMap().get(senderWallet.getAddress()));
        assertEquals(50, transaction.getOutputMap().get("abc"));

        assertEquals(55, transaction.getInput().getBalance());
        assertEquals(transaction.getCreateTime(), transaction.getInput().getCreateTime());
        assertEquals(senderWallet.getPublicKey(), transaction.getInput().getPublicKey());
//        assertEquals(CryptoUtil.generateSignature(), transaction.getInput().getSignature());
    }

    /*@ParameterizedTest
    @ValueSource(ints = {1,2,3,4})
    void sampleTestWhichFailsBecauseAllAreNotTrueButYouGetTheJist(int number)
    {
        assertAll(
                () -> assertEquals(1, number),
                () -> assertEquals(2, number),
                () -> assertEquals(3, number),
                () -> assertEquals(4, number));
    }*/
}
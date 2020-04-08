package com.conal.blockchain.crypto;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CryptoUtil
{
    public static KeyPair generateKey()
    {
        try
        {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom secRandom = new SecureRandom();
            keyPairGenerator.initialize(2048, secRandom);//todo find out a smaller number?
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateSignature(String data, KeyPair keyPair)
    {
        try
        {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(keyPair.getPrivate());
            sign.update(data.getBytes(StandardCharsets.UTF_8));
            return sign.sign();
        }
        catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String cryptoHash(String... components)
    {
        try
        {
            // a digest is a result of a hash
            // create the hash using SHA-256 (256 bits for the hash)
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String joinedComponents = Arrays.stream(components)
                    .sorted(Comparator.naturalOrder()) // is this necessary?
                    .collect(Collectors.joining());
            messageDigest.update(joinedComponents.getBytes(StandardCharsets.UTF_8));
            byte[] digest = messageDigest.digest();
            return new String(digest, StandardCharsets.UTF_8); // this gets us back UTF-8 string
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Hash algorithm not found");
        }
    }

    public static boolean verifySignature(byte[] signature, PublicKey publicKey, String data)
    {
        try
        {
            // todo - might need to perform a reverse hash of the data?
            Signature sign = Signature.getInstance("SHA256withDSA");
            sign.initVerify(publicKey);
            sign.update(data.getBytes(StandardCharsets.UTF_8));
            return sign.verify(signature);
        }
        catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
}

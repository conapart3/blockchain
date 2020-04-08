package com.conal.blockchain.exception;

import java.text.MessageFormat;

public class BlockchainRuntimeException extends RuntimeException
{
    public BlockchainRuntimeException(String messageFormat, String... args)
    {
        super(MessageFormat.format(messageFormat, args));
    }
}

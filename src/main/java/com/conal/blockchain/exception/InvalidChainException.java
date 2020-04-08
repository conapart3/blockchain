package com.conal.blockchain.exception;

import java.text.MessageFormat;

public class InvalidChainException extends Exception
{
    public InvalidChainException(String message, String... parameters)
    {
        super(MessageFormat.format(message, parameters));
    }
}

package com.conal.blockchain.redis;

public interface MessagePublisher
{
    void publish(Object message);
}

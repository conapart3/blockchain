package com.conal.blockchain.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class RedisMessagePublisher implements MessagePublisher
{
    private StringRedisTemplate redisTemplate;
    private ChannelTopic topic;

    //todo sort this topic out - one redisMessagePublsiher per topic?
    public RedisMessagePublisher(StringRedisTemplate redisTemplate, ChannelTopic topic)
    {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(Object message)
    {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}

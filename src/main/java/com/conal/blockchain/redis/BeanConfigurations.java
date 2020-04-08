package com.conal.blockchain.redis;

import com.conal.blockchain.core.Blockchain;
import com.conal.blockchain.core.BlockchainController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@ComponentScan(basePackages = "com.conal.blockchain")
public class BeanConfigurations
{
    @Bean
    JedisConnectionFactory jedisConnectionFactory()
    {
        return new JedisConnectionFactory();
    }

    @Bean
    public StringRedisTemplate redisTemplate()
    {
        // template used to send messages
        final StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Blockchain.class));
        return template;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter()
    {
        return new MessageListenerAdapter(new RedisMessageSubscriber(blockchainController()));
    }

    @Bean
    public BlockchainController blockchainController()
    {
        return new BlockchainController(mainTopicPublisher(), transactionTopicPublisher());
    }

    @Bean
    RedisMessageListenerContainer messageListenerContainer()
    {
        // container used to listen to the topic, probably can have multiple listeners?
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        // setting the messagelistener to listen to the topic MessageChannel bean below.
        container.addMessageListener(messageListenerAdapter(), mainTopic());
        container.addMessageListener(messageListenerAdapter(), transactionTopic());
        return container;
    }

    @Bean
    RedisMessagePublisher mainTopicPublisher()
    {
        return new RedisMessagePublisher(redisTemplate(), mainTopic());
    }

    @Bean
    RedisMessagePublisher transactionTopicPublisher()
    {
        return new RedisMessagePublisher(redisTemplate(), transactionTopic());
    }

    @Bean
    ChannelTopic mainTopic()
    {
        return new ChannelTopic("BLOCKCHAIN");
    }

    @Bean
    ChannelTopic transactionTopic()
    {
        return new ChannelTopic("TRANSACTION");
    }
}

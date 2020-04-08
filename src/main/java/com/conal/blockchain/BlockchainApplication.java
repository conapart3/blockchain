package com.conal.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class BlockchainApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(BlockchainApplication.class, args);

//		Blockchain blockchain = new Blockchain();
    }

}

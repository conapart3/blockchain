package com.conal.blockchain.api;

import com.conal.blockchain.api.dto.AddTransactionRequestDto;
import com.conal.blockchain.core.Block;
import com.conal.blockchain.core.BlockchainController;
import com.conal.blockchain.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Map;

@EnableWebMvc
@RestController
public class BlockchainApi
{
    private BlockchainController blockchainController;

    @Autowired
    public BlockchainApi(BlockchainController blockchainController)
    {
        this.blockchainController = blockchainController;
    }

    @GetMapping("/api/blocks")
    @ResponseBody
    public List<Block> getBlocks()
    {
        return blockchainController.getBlockChain();
    }

    @PostMapping("/api/mineBlock")
    public String mineBlock(@RequestBody String data) throws Exception
    {
        if (StringUtils.isEmpty(data))
        {
            throw new Exception("data cannot be null.");
        }
        blockchainController.mineBlock(data);

        return "redirect:/api/blocks";
    }

    @PostMapping("/api/transaction")
    @ResponseBody
    public Transaction transaction(@RequestBody AddTransactionRequestDto body) throws Exception
    {
        return blockchainController.addOrUpdateTransaction(body.getRecipient(), body.getAmount());
    }

    @GetMapping("/api/transactionPool")
    @ResponseBody
    public Map<String, Transaction> transactionPool() throws Exception
    {
        return blockchainController.getTransactionPool();
    }

}

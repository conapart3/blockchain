package com.conal.blockchain.api.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddTransactionRequestDto
{
    private String recipient;
    private double amount;
}

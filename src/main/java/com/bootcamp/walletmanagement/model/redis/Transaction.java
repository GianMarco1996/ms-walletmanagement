package com.bootcamp.walletmanagement.model.redis;

import lombok.Data;

@Data
public class Transaction {
    private String id;

    private String category;

    private String type;

    private String accountId;

    private Double amount;

    private String transactionDate;

    private String description;

    private String mobile;
}
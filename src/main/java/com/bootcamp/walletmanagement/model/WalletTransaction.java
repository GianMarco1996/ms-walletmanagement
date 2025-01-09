package com.bootcamp.walletmanagement.model;

import lombok.Data;

@Data
public class WalletTransaction {
    private String id;

    private String category;

    private String type;

    private String accountId;

    private Double amount;

    private String transactionDate;

    private String description;
}
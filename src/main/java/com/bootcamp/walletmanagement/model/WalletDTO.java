package com.bootcamp.walletmanagement.model;

import lombok.Data;

import java.util.List;

@Data
public class WalletDTO {
    private String id;

    private String fullNames;

    private String documentType;

    private String documentNumber;

    private String mobile;

    private String mobileImei;

    private String email;

    private Double currentBalance;

    private String debitCardId;

    private Boolean status;

    private List<WalletTransaction> transactions;
}
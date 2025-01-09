package com.bootcamp.walletmanagement.model;

import lombok.Data;

@Data
public class YankearWalletDTO {

    private String mobileDestination;

    private Double amount;

    private String description;
}
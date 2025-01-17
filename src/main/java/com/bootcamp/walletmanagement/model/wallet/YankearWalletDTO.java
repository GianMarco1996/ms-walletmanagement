package com.bootcamp.walletmanagement.model.wallet;

import lombok.Data;

@Data
public class YankearWalletDTO {

    private String mobileDestination;

    private Double amount;

    private String description;
}
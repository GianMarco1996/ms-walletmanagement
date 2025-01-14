package com.bootcamp.walletmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "wallets")
public class Wallet {
    @Id
    private String id;

    private String fullNames;

    private String documentType;

    private String documentNumber;

    private String mobile;

    private String mobileImei;

    private String email;

    private Double currentBalance;

    private Boolean status;

    private String debitCardId;

    private Integer bootCoin;
}
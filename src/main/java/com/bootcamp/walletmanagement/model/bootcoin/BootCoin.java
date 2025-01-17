package com.bootcamp.walletmanagement.model.bootcoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bootCoins")
public class BootCoin {
    @Id
    private String id;

    private String type;

    private Integer bootCoinAmount;

    private String paymentMode;

    private String walletId;

    private String productId;
}
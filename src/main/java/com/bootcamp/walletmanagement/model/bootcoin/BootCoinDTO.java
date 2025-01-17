package com.bootcamp.walletmanagement.model.bootcoin;

import com.bootcamp.walletmanagement.model.redis.Product;
import lombok.Data;

@Data
public class BootCoinDTO {
    private String id;

    private String type;

    private Integer bootCoinAmount;

    private String paymentMode;

    private String walletId;

    private String productId;

    private Product product;
}
package com.bootcamp.walletmanagement.model.redis;

import lombok.Data;

@Data
public class Product {
    private String id;

    private String name;

    private String type;

    private String category;

    private String typeCard;

    private String description;

    private Integer monthlyMovements;

    private Double commissionMaintenance;

    private Integer movementDay;

    private Double pricePurchase;

    private Double priceSale;
}
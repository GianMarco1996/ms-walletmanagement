package com.bootcamp.walletmanagement.service.redis.product;

import com.bootcamp.walletmanagement.model.redis.Product;

public interface ProductRedisService {
    Product getProductRedis(String productId);
}
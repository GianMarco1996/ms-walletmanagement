package com.bootcamp.walletmanagement.service.redis.transaction;

import com.bootcamp.walletmanagement.model.redis.Transaction;

import java.util.List;

public interface RedisService {
    List<Transaction> findAllTransactionsRedis(String mobile);
}
package com.bootcamp.walletmanagement.repository.bootcoin;

import com.bootcamp.walletmanagement.model.bootcoin.BootCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootCoinRepository extends ReactiveMongoRepository<BootCoin, String> {
}
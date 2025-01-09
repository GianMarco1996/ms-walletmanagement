package com.bootcamp.walletmanagement.repository;

import com.bootcamp.walletmanagement.model.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {
    Mono<Wallet> findWalletByMobile(String mobile);
}
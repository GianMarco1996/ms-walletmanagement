package com.bootcamp.walletmanagement.service;

import com.bootcamp.walletmanagement.model.Wallet;
import com.bootcamp.walletmanagement.model.WalletDTO;
import com.bootcamp.walletmanagement.model.YankearWalletDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {

    Flux<Wallet> getWallets(String status);

    Mono<Wallet> getWallet(String id);

    Mono<Wallet> registerWallet(Mono<Wallet> wallet);

    Mono<Wallet> updateWallet(String id, Mono<Wallet> wallet);

    Mono<String> updateWalletStatus(String id, String status);

    Mono<WalletDTO> getWalletTransactions(String id);

    Mono<String> associateDebitCard(String id, String debitCardId);

    Mono<String> yankearWallet(String id, Mono<YankearWalletDTO> yankearWallet);
}
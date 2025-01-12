package com.bootcamp.walletmanagement.controller;

import com.bootcamp.walletmanagement.api.WalletApi;
import com.bootcamp.walletmanagement.mapper.WalletMapper;
import com.bootcamp.walletmanagement.model.WalletDetailResponse;
import com.bootcamp.walletmanagement.model.WalletRequest;
import com.bootcamp.walletmanagement.model.WalletResponse;
import com.bootcamp.walletmanagement.model.YankearWallet;
import com.bootcamp.walletmanagement.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class WalletController implements WalletApi {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Mono<ResponseEntity<WalletResponse>> getWallet(String id, ServerWebExchange exchange) {
        return walletService.getWallet(id)
                .map(wallet -> walletMapper.documentToModel(wallet))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WalletDetailResponse>> getWalletTransactions(String id, ServerWebExchange exchange) {
        return walletService.getWalletTransactions(id)
                .map(wallet -> walletMapper.dtoToModel(wallet))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<WalletResponse>>> getWallets(String status, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok().body(walletService.getWallets(status)
                        .map(wallet -> walletMapper.documentToModel(wallet))));
    }

    @Override
    public Mono<ResponseEntity<Object>> registerWallet(Mono<WalletRequest> walletRequest, ServerWebExchange exchange) {
        return walletService.registerWallet(walletRequest.map(walletMapper::modelToDocument))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Object>> updateWallet(String id, Mono<WalletRequest> walletRequest, ServerWebExchange exchange) {
        return walletService.updateWallet(id, walletRequest.map(walletMapper::updateModelToDocument))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Object>> updateWalletStatus(String id, String status, ServerWebExchange exchange) {
        return walletService.updateWalletStatus(id, status)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Object>> associateDebitCard(String id, String debitCardId, ServerWebExchange exchange) {
        return walletService.associateDebitCard(id, debitCardId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Object>> yankearWallet(String id, Mono<YankearWallet> yankearWallet, ServerWebExchange exchange) {
        return walletService.yankearWallet(id, yankearWallet.map(walletMapper::modelToDto))
                .map(ResponseEntity::ok);
    }
}
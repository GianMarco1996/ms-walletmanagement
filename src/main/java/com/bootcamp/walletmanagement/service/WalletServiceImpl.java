package com.bootcamp.walletmanagement.service;

import com.bootcamp.walletmanagement.mapper.WalletMapper;
import com.bootcamp.walletmanagement.model.Wallet;
import com.bootcamp.walletmanagement.model.WalletDTO;
import com.bootcamp.walletmanagement.model.YankearWalletDTO;
import com.bootcamp.walletmanagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Flux<Wallet> getWallets(String status) {
        return walletRepository.findAll()
                .flatMap(wallet -> status.equals("T")
                        ? Flux.just(wallet)
                        : status.equals("A") ? Flux.just(wallet).filter(w -> w.getStatus() == Boolean.TRUE)
                        : Flux.just(wallet).filter(w -> w.getStatus() == Boolean.FALSE)
                );
    }

    @Override
    public Mono<Wallet> getWallet(String id) {
        return walletRepository.findById(id);
    }

    @Override
    public Mono<Wallet> registerWallet(Mono<Wallet> wallet) {
        return wallet.flatMap(walletRepository::save);
    }

    @Override
    public Mono<Wallet> updateWallet(String id, Mono<Wallet> wallet) {
        return walletRepository.findById(id)
                .flatMap(w -> wallet.map(walletMono -> {
                    walletMono.setCurrentBalance(w.getCurrentBalance());
                    walletMono.setStatus(w.getStatus());
                    return walletMono;
                }))
                .doOnNext(w -> w.setId(id))
                .flatMap(walletRepository::save);
    }

    @Override
    public Mono<String> updateWalletStatus(String id, String status) {
        return Mono.just(status)
                .filter(s -> s.equals("A") || s.equals("I"))
                .switchIfEmpty(Mono.error(new Exception("El estado ingresado es el incorrecto")))
                .flatMap(s -> walletRepository.findById(id)
                        .map(wallet -> {
                            if (s.equals("A")) {
                                wallet.setStatus(Boolean.TRUE);
                            } else {
                                wallet.setStatus(Boolean.FALSE);
                            }
                            return wallet;
                        })
                        .flatMap(walletRepository::save)
                        .flatMap(w -> status.equals("A")
                                ? Mono.just("Su monedero fue activado exitosamente")
                                : Mono.just("Su monedero fue desactivado"))
                );

    }

    @Override
    public Mono<WalletDTO> getWalletTransactions(String id) {
        return walletRepository.findById(id)
                .map(wallet -> walletMapper.documentToDto(wallet))
                .map(wallet -> {
                    wallet.setTransactions(new ArrayList<>());
                    return wallet;
                });
    }

    @Override
    public Mono<String> associateDebitCard(String id, String debitCardId) {
        return walletRepository.findById(id)
                .map(wallet -> {
                    wallet.setDebitCardId(debitCardId);
                    return wallet;
                })
                .flatMap(walletRepository::save)
                .flatMap(wallet -> Mono.just("Tarjeta de debito asociado correctamente"));
    }

    @Override
    public Mono<String> yankearWallet(String id, Mono<YankearWalletDTO> yankearWallet) {
        return yankearWallet.flatMap(yanki -> walletRepository.findById(id)
                        .map(wallet -> {
                            if (Objects.isNull(wallet.getDebitCardId())) {
                                if (wallet.getCurrentBalance() < yanki.getAmount()) {
                                    throw new IllegalArgumentException("No tiene saldo suficiente para realizar el pago.");
                                }
                                wallet.setCurrentBalance(wallet.getCurrentBalance() - yanki.getAmount());
                            } else {
                                System.out.println("Se actualiza el monto en la cuenta principal de la TD");
                            }
                            return wallet;
                        })
                        .flatMap(walletRepository::save)
                        .flatMap(wallet -> walletRepository.findWalletByMobile(yanki.getMobileDestination())
                                .map(walletDestination -> {
                                    if (Objects.isNull(walletDestination.getDebitCardId())) {
                                        walletDestination.setCurrentBalance(walletDestination.getCurrentBalance() + yanki.getAmount());
                                    } else {
                                        System.out.println("Se actualiza el monto en la cuenta principal de la TD");
                                    }
                                    return walletDestination;
                                })
                                .flatMap(walletRepository::save)
                        )
                )
                .flatMap(wallet -> Mono.just("Se Yankeo correctamente"));
    }
}
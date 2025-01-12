package com.bootcamp.walletmanagement.service.wallet;

import com.bootcamp.walletmanagement.mapper.WalletMapper;
import com.bootcamp.walletmanagement.messaging.KafkaProducer;
import com.bootcamp.walletmanagement.messaging.KafkaTransaction;
import com.bootcamp.walletmanagement.model.Wallet;
import com.bootcamp.walletmanagement.model.WalletDTO;
import com.bootcamp.walletmanagement.model.YankearWalletDTO;
import com.bootcamp.walletmanagement.repository.WalletRepository;
import com.bootcamp.walletmanagement.service.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private RedisService redisService;

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
                    wallet.setTransactions(redisService.findAllTransactionsRedis(wallet.getMobile()));
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
                        .flatMap(wallet -> {
                            if (Objects.isNull(wallet.getDebitCardId())) {
                                if (wallet.getCurrentBalance() < yanki.getAmount()) {
                                    throw new IllegalArgumentException("No tiene saldo suficiente para realizar el pago.");
                                }
                                wallet.setCurrentBalance(wallet.getCurrentBalance() - yanki.getAmount());
                                return walletRepository.save(wallet)
                                        .flatMap(wa -> updateYankiOrigin(Mono.just(wa), yanki));
                            } else {
                                System.out.println("Se actualiza el monto en la cuenta principal de la TD");
                                return Mono.just(wallet).flatMap(wa -> updateYankiOrigin(Mono.just(wa), yanki));
                            }
                        })
                        .flatMap(transactions -> walletRepository.findWalletByMobile(yanki.getMobileDestination())
                                .flatMap(walletDestination -> {
                                    if (Objects.isNull(walletDestination.getDebitCardId())) {
                                        walletDestination.setCurrentBalance(walletDestination.getCurrentBalance() + yanki.getAmount());
                                        return walletRepository.save(walletDestination)
                                                .flatMap(wa -> updateYankiDestination(transactions, yanki));
                                    } else {
                                        System.out.println("Se actualiza el monto en la cuenta principal de la TD");
                                        return Mono.just(walletDestination).flatMap(wa -> updateYankiDestination(transactions, yanki));
                                    }
                                }))
                ).map(transactions -> {
                    sendMessage(transactions);
                    return transactions;
                }).flatMap(wallet -> Mono.just("Se Yankeo correctamente"));
    }

    private Mono<List<KafkaTransaction>> updateYankiOrigin(Mono<Wallet> wallet, YankearWalletDTO yanki) {
        return wallet.flatMap(wa -> {
            List<KafkaTransaction> transactions = new ArrayList<>();
            KafkaTransaction transaction = new KafkaTransaction();
            transaction.setCategory("Yanki");
            transaction.setType("Movimiento");
            transaction.setMobile(wa.getMobile());
            transaction.setTransactionDate(LocalDate.now().toString());
            transaction.setAmount(yanki.getAmount());
            transaction.setDescription(Objects.isNull(yanki.getDescription()) ? "Yanki realizado" : yanki.getDescription());
            transactions.add(transaction);
            return Mono.just(transactions);
        });
    }

    private Mono<List<KafkaTransaction>> updateYankiDestination(List<KafkaTransaction> transactions, YankearWalletDTO yanki) {
        return Mono.just(transactions).map(tr -> {
            KafkaTransaction transaction = new KafkaTransaction();
            transaction.setCategory("Yanki");
            transaction.setType("Movimiento");
            transaction.setTransactionDate(LocalDate.now().toString());
            transaction.setMobile(yanki.getMobileDestination());
            transaction.setAmount(yanki.getAmount());
            transaction.setDescription(Objects.isNull(yanki.getDescription()) ? "Yanki recibido" : yanki.getDescription());
            tr.add(transaction);
            return tr;
        });
    }

    private void sendMessage(List<KafkaTransaction> transactions) {
        try {
            kafkaProducer.send(transactions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
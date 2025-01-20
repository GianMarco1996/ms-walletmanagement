package com.bootcamp.walletmanagement.service.wallet;

import com.bootcamp.walletmanagement.mapper.wallet.WalletMapper;
import com.bootcamp.walletmanagement.messaging.KafkaProducer;
import com.bootcamp.walletmanagement.messaging.KafkaTransaction;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoinDTO;
import com.bootcamp.walletmanagement.model.wallet.Wallet;
import com.bootcamp.walletmanagement.model.wallet.WalletDTO;
import com.bootcamp.walletmanagement.model.wallet.YankearWalletDTO;
import com.bootcamp.walletmanagement.repository.wallet.WalletRepository;
import com.bootcamp.walletmanagement.service.bootcoin.BootCoinService;
import com.bootcamp.walletmanagement.service.redis.transaction.RedisService;
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

    @Autowired
    private BootCoinService bootCoinService;

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

    @Override
    public Mono<String> associateBootCoin(String id) {
        return walletRepository.findById(id)
                .map(wallet -> {
                    wallet.setBootCoin(0);
                    return wallet;
                })
                .flatMap(walletRepository::save)
                .flatMap(wallet -> Mono.just("BootCoin asociado correctamente"));
    }

    @Override
    public Mono<String> bootCoinTransaction(String id, String bootCoinId) {
        return bootCoinService.getBootCoinDetail(bootCoinId)
                .flatMap(bootCoin -> walletRepository.findById(id)
                        .flatMap(wallet -> {
                            Double amount;
                            if (Objects.isNull(wallet.getBootCoin())) {
                                throw new IllegalArgumentException("Su monedero no se encuentra asociado con el bootCoin.");
                            }
                            if (bootCoin.getType().equals("Compra")) {
                                if (wallet.getBootCoin() < bootCoin.getBootCoinAmount()) {
                                    throw new IllegalArgumentException("Su monedero no cuenta con los bootCoin suficiente para vender.");
                                }
                                amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPricePurchase();
                                wallet.setBootCoin(wallet.getBootCoin() - bootCoin.getBootCoinAmount());
                                wallet.setCurrentBalance(wallet.getCurrentBalance() + amount);
                            } else {
                                amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPriceSale();
                                if (wallet.getCurrentBalance() < amount) {
                                    throw new IllegalArgumentException("No tiene saldo suficiente para realizar el pago.");
                                }
                                wallet.setBootCoin(wallet.getBootCoin() + bootCoin.getBootCoinAmount());
                                wallet.setCurrentBalance(wallet.getCurrentBalance() - amount);
                            }
                            return walletRepository.save(wallet)
                                    .flatMap(wa -> updateYankiBootCoinOrigin(Mono.just(wa), bootCoin));
                        })
                        .flatMap(transactions -> walletRepository.findById(bootCoin.getWalletId())
                                .flatMap(wallet -> {
                                    Double amount;
                                    if (bootCoin.getType().equals("Compra")) {
                                        amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPricePurchase();
                                        if (wallet.getCurrentBalance() < amount) {
                                            throw new IllegalArgumentException("El monedero comprador no tiene suficiente saldo.");
                                        }
                                        wallet.setBootCoin(wallet.getBootCoin() + bootCoin.getBootCoinAmount());
                                        wallet.setCurrentBalance(wallet.getCurrentBalance() - amount);
                                    } else {
                                        amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPriceSale();
                                        wallet.setBootCoin(wallet.getBootCoin() - bootCoin.getBootCoinAmount());
                                        wallet.setCurrentBalance(wallet.getCurrentBalance() + amount);
                                    }
                                    return walletRepository.save(wallet)
                                            .flatMap(wa -> updateYankiBootCoinDestination(transactions, bootCoin, wallet.getMobile()));
                                }))
                )
                .map(transactions -> {
                    sendMessage(transactions);
                    return transactions;
                })
                .flatMap(t -> bootCoinService.deleteBootCoin(bootCoinId))
                .then(Mono.just("Se realizo la operación correctamente."));
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

    private Mono<List<KafkaTransaction>> updateYankiBootCoinOrigin(Mono<Wallet> wallet, BootCoinDTO bootCoin) {
        return wallet.flatMap(wa -> {
            List<KafkaTransaction> transactions = new ArrayList<>();
            KafkaTransaction transaction = new KafkaTransaction();
            transaction.setCategory("Yanki");
            transaction.setType("Movimiento");
            transaction.setMobile(wa.getMobile());
            transaction.setTransactionDate(LocalDate.now().toString());

            double amount;
            String description;
            if (bootCoin.getType().equals("Compra")) {
                amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPricePurchase();
                description = "Se realizo la venta de bootCoin ".concat(bootCoin.getBootCoinAmount().toString());
            } else {
                amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPriceSale();
                description = "Se realizo la compra de bootCoin ".concat(bootCoin.getBootCoinAmount().toString());
            }
            transaction.setAmount(amount);
            transaction.setDescription(description);

            transactions.add(transaction);
            return Mono.just(transactions);
        });
    }

    private Mono<List<KafkaTransaction>> updateYankiBootCoinDestination(List<KafkaTransaction> transactions, BootCoinDTO bootCoin, String mobile) {
        return Mono.just(transactions).map(tr -> {
            KafkaTransaction transaction = new KafkaTransaction();
            transaction.setCategory("Yanki");
            transaction.setType("Movimiento");
            transaction.setTransactionDate(LocalDate.now().toString());
            transaction.setMobile(mobile);

            double amount;
            String description;
            if (bootCoin.getType().equals("Compra")) {
                amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPricePurchase();
                description = "Se realizo la compra de bootCoin ".concat(bootCoin.getBootCoinAmount().toString());
            } else {
                amount = bootCoin.getBootCoinAmount() * bootCoin.getProduct().getPriceSale();
                description = "Se realizo la venta de bootCoin ".concat(bootCoin.getBootCoinAmount().toString());
            }
            transaction.setAmount(amount);
            transaction.setDescription(description);

            tr.add(transaction);
            return tr;
        });
    }
}
package com.bootcamp.walletmanagement.service;

import com.bootcamp.walletmanagement.mapper.wallet.WalletMapper;
import com.bootcamp.walletmanagement.messaging.KafkaProducer;
import com.bootcamp.walletmanagement.model.wallet.Wallet;
import com.bootcamp.walletmanagement.model.wallet.WalletDTO;
import com.bootcamp.walletmanagement.model.wallet.YankearWalletDTO;
import com.bootcamp.walletmanagement.model.redis.Transaction;
import com.bootcamp.walletmanagement.repository.wallet.WalletRepository;
import com.bootcamp.walletmanagement.service.redis.transaction.RedisService;
import com.bootcamp.walletmanagement.service.wallet.WalletServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    @InjectMocks
    WalletServiceImpl walletService;

    @Mock
    WalletRepository walletRepository;

    @Mock
    WalletMapper walletMapper;

    @Mock
    KafkaProducer kafkaProducer;

    @Mock
    RedisService redisService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWalletsTest_All() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");
        List<Wallet> wallets = List.of(wallet);

        Mockito.when(walletRepository.findAll()).thenReturn(Flux.fromIterable(wallets));

        Flux<Wallet> result = walletService.getWallets("T");

        StepVerifier.create(result)
                .expectNext(wallet)
                .verifyComplete();

        Mockito.verify(walletRepository).findAll();
    }

    @Test
    void getWalletsTest_Active() {
        Wallet walletActive = new Wallet();
        walletActive.setId("677ff06ddeea8c5eb02f44f5");
        walletActive.setFullNames("Pepe");
        walletActive.setDocumentType("DNI");
        walletActive.setDocumentNumber("87654321");
        walletActive.setMobile("987654321");
        walletActive.setMobileImei("123456789087654");
        walletActive.setEmail("pepe@correo.com");
        walletActive.setCurrentBalance(32D);
        walletActive.setStatus(true);
        walletActive.setDebitCardId("677ff06ddeea8c5eb02f44f3");
        List<Wallet> wallets = List.of(walletActive);

        Mockito.when(walletRepository.findAll()).thenReturn(Flux.fromIterable(wallets));

        Flux<Wallet> result = walletService.getWallets("A");

        StepVerifier.create(result)
                .expectNext(walletActive)
                .verifyComplete();

        Mockito.verify(walletRepository).findAll();
    }

    @Test
    void getWalletsTest_Inactive() {
        Wallet walletInactive = new Wallet();
        walletInactive.setId("677ff06ddeea8c5eb02f44f5");
        walletInactive.setFullNames("Pepe");
        walletInactive.setDocumentType("DNI");
        walletInactive.setDocumentNumber("87654321");
        walletInactive.setMobile("987654321");
        walletInactive.setMobileImei("123456789087654");
        walletInactive.setEmail("pepe@correo.com");
        walletInactive.setCurrentBalance(32D);
        walletInactive.setStatus(false);
        walletInactive.setDebitCardId("677ff06ddeea8c5eb02f44f3");
        List<Wallet> wallets = List.of(walletInactive);

        Mockito.when(walletRepository.findAll()).thenReturn(Flux.fromIterable(wallets));

        Flux<Wallet> result = walletService.getWallets("I");

        StepVerifier.create(result)
                .expectNext(walletInactive)
                .verifyComplete();

        Mockito.verify(walletRepository).findAll();
    }

    @Test
    void getWallet() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = walletService.getWallet("677ff06ddeea8c5eb02f44f5");

        StepVerifier.create(result)
                .expectNext(wallet)
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
    }

    @Test
    void registerWallet() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = walletService.registerWallet(Mono.just(wallet));

        StepVerifier.create(result)
                .expectNext(wallet)
                .verifyComplete();

        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }

    @Test
    void updateWallet() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = walletService.updateWallet(wallet.getId(), Mono.just(wallet));

        StepVerifier.create(result)
                .expectNextMatches(walletResult -> {
                    Assertions.assertEquals(wallet.getId(), walletResult.getId());
                    Assertions.assertEquals(true, walletResult.getStatus());
                    return true;
                })
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }

    @Test
    void updateWalletStatus_Active() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<String> result = walletService.updateWalletStatus(wallet.getId(), "A");

        StepVerifier.create(result)
                .expectNextMatches(walletResult -> {
                    Assertions.assertEquals("Su monedero fue activado exitosamente", walletResult);
                    return true;
                })
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }

    @Test
    void updateWalletStatus_Inactive() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<String> result = walletService.updateWalletStatus(wallet.getId(), "I");

        StepVerifier.create(result)
                .expectNextMatches(walletResult -> {
                    Assertions.assertEquals("Su monedero fue desactivado", walletResult);
                    return true;
                })
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }

    @Test
    void getWalletTransactions() {
        Transaction transaction = new Transaction();
        transaction.setId("677ff06ddeea8c5eb02f44f5");
        transaction.setCategory("Yanki");
        transaction.setType("Movimiento");
        transaction.setAmount(20D);
        transaction.setTransactionDate("2025-01-11");
        transaction.setDescription("Yanki recibido");
        transaction.setMobile("987654321");
        List<Transaction> transactions = List.of(transaction);


        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId("677ff06ddeea8c5eb02f44f5");
        walletDTO.setFullNames("Pepe");
        walletDTO.setDocumentType("DNI");
        walletDTO.setDocumentNumber("87654321");
        walletDTO.setMobile("987654321");
        walletDTO.setMobileImei("123456789087654");
        walletDTO.setEmail("pepe@correo.com");
        walletDTO.setCurrentBalance(32D);
        walletDTO.setStatus(true);
        walletDTO.setDebitCardId("677ff06ddeea8c5eb02f44f3");
        walletDTO.setTransactions(transactions);

        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mockito.when(walletMapper.documentToDto(Mockito.any(Wallet.class))).thenReturn(walletDTO);

        Mockito.when(redisService.findAllTransactionsRedis(Mockito.anyString())).thenReturn(transactions);

        Mono<WalletDTO> result = walletService.getWalletTransactions(walletDTO.getId());

        StepVerifier.create(result)
                .expectNextMatches(walletResult -> {
                    Assertions.assertEquals("987654321", walletResult.getMobile());
                    Assertions.assertNotNull(walletResult.getTransactions());
                    return true;
                })
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
    }

    @Test
    void associateDebitCard() {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);
        wallet.setDebitCardId("677ff06ddeea8c5eb02f44f3");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<String> result = walletService.associateDebitCard(wallet.getId(), "677ff06ddeea8c5eb02f44f3");

        StepVerifier.create(result)
                .expectNextMatches(walletResult -> {
                    Assertions.assertEquals("Tarjeta de debito asociado correctamente", walletResult);
                    return true;
                })
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }

    @Test
    void yankearWallet() throws JsonProcessingException {
        Wallet wallet = new Wallet();
        wallet.setId("677ff06ddeea8c5eb02f44f5");
        wallet.setFullNames("Pepe");
        wallet.setDocumentType("DNI");
        wallet.setDocumentNumber("87654321");
        wallet.setMobile("987654321");
        wallet.setMobileImei("123456789087654");
        wallet.setEmail("pepe@correo.com");
        wallet.setCurrentBalance(32D);
        wallet.setStatus(true);

        Wallet wallet2 = new Wallet();
        wallet2.setId("677ff06ddeea8c5eb02f44f5");
        wallet2.setFullNames("Pepe");
        wallet2.setDocumentType("DNI");
        wallet2.setDocumentNumber("87654321");
        wallet2.setMobile("987654333");
        wallet2.setMobileImei("123456789087654");
        wallet2.setEmail("pepe@correo.com");
        wallet2.setCurrentBalance(32D);
        wallet2.setStatus(true);

        YankearWalletDTO yankearWalletDTO = new YankearWalletDTO();
        yankearWalletDTO.setMobileDestination("987654333");
        yankearWalletDTO.setAmount(10D);
        yankearWalletDTO.setDescription("Yanki realizado");

        Mockito.when(walletRepository.findById(Mockito.anyString())).thenReturn(Mono.just(wallet));

        Mockito.when(walletRepository.findWalletByMobile(Mockito.anyString())).thenReturn(Mono.just(wallet2));

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(Mono.just(wallet2));

        Mockito.doNothing().when(kafkaProducer).send(Mockito.anyList());

        Mono<String> result = walletService.yankearWallet(wallet.getId(), Mono.just(yankearWalletDTO));

        StepVerifier.create(result)
                .expectNextMatches(walletResult -> {
                    Assertions.assertEquals("Se Yankeo correctamente", walletResult);
                    return true;
                })
                .verifyComplete();

        Mockito.verify(walletRepository).findById(Mockito.anyString());
        Mockito.verify(walletRepository).findWalletByMobile(Mockito.anyString());
    }
}
package com.bootcamp.walletmanagement.service;

import com.bootcamp.walletmanagement.mapper.bootcoin.BootCoinMapper;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoin;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoinDTO;
import com.bootcamp.walletmanagement.model.redis.Product;
import com.bootcamp.walletmanagement.repository.bootcoin.BootCoinRepository;
import com.bootcamp.walletmanagement.service.bootcoin.BootCoinServiceImpl;
import com.bootcamp.walletmanagement.service.redis.product.ProductRedisService;
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

@ExtendWith(MockitoExtension.class)
public class BootCoinServiceImplTest {

    @InjectMocks
    BootCoinServiceImpl bootCoinService;

    @Mock
    BootCoinRepository bootCoinRepository;

    @Mock
    ProductRedisService productRedisService;

    @Mock
    BootCoinMapper bootCoinMapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBootCoin_All() {
        Product product = new Product();
        product.setId("6789a9f814020f5b783bcf32");
        product.setName("BootCoin");
        product.setType("Pasivo");
        product.setCategory("BootCoin");
        product.setDescription("Moneda Virtual");
        product.setPricePurchase(3.14);
        product.setPriceSale(3.25);

        BootCoinDTO bootCoinDto = new BootCoinDTO();
        bootCoinDto.setId("677ff06ddeea8c5eb02f44f6");
        bootCoinDto.setType("Venta");
        bootCoinDto.setBootCoinAmount(5);
        bootCoinDto.setPaymentMode("Yanki");
        bootCoinDto.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoinDto.setProductId("6789a9f814020f5b783bcf32");
        bootCoinDto.setProduct(product);

        BootCoin bootCoin = new BootCoin();
        bootCoin.setId("677ff06ddeea8c5eb02f44f6");
        bootCoin.setType("Venta");
        bootCoin.setBootCoinAmount(5);
        bootCoin.setPaymentMode("Yanki");
        bootCoin.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoin.setProductId("6789a9f814020f5b783bcf32");

        Mockito.when(bootCoinRepository.findAll()).thenReturn(Flux.just(bootCoin));

        Mockito.when(bootCoinMapper.documentToDto(Mockito.any(BootCoin.class))).thenReturn(bootCoinDto);

        Mockito.when(productRedisService.getProductRedis(Mockito.anyString())).thenReturn(product);

        Flux<BootCoinDTO> result = bootCoinService.getBootCoin("T");

        StepVerifier.create(result)
                .expectNext(bootCoinDto)
                .verifyComplete();

        Mockito.verify(bootCoinRepository).findAll();
    }

    @Test
    void getBootCoin_buys() {
        Product product = new Product();
        product.setId("6789a9f814020f5b783bcf32");
        product.setName("BootCoin");
        product.setType("Pasivo");
        product.setCategory("BootCoin");
        product.setDescription("Moneda Virtual");
        product.setPricePurchase(3.14);
        product.setPriceSale(3.25);

        BootCoinDTO bootCoinDto = new BootCoinDTO();
        bootCoinDto.setId("677ff06ddeea8c5eb02f44f6");
        bootCoinDto.setType("Compra");
        bootCoinDto.setBootCoinAmount(5);
        bootCoinDto.setPaymentMode("Yanki");
        bootCoinDto.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoinDto.setProductId("6789a9f814020f5b783bcf32");
        bootCoinDto.setProduct(product);

        BootCoin bootCoin = new BootCoin();
        bootCoin.setId("677ff06ddeea8c5eb02f44f6");
        bootCoin.setType("Venta");
        bootCoin.setBootCoinAmount(5);
        bootCoin.setPaymentMode("Yanki");
        bootCoin.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoin.setProductId("6789a9f814020f5b783bcf32");

        Mockito.when(bootCoinRepository.findAll()).thenReturn(Flux.just(bootCoin));

        Mockito.when(bootCoinMapper.documentToDto(Mockito.any(BootCoin.class))).thenReturn(bootCoinDto);

        Mockito.when(productRedisService.getProductRedis(Mockito.anyString())).thenReturn(product);

        Flux<BootCoinDTO> result = bootCoinService.getBootCoin("C");

        StepVerifier.create(result)
                .expectNext(bootCoinDto)
                .verifyComplete();

        Mockito.verify(bootCoinRepository).findAll();
    }

    @Test
    void getBootCoin_sale() {
        Product product = new Product();
        product.setId("6789a9f814020f5b783bcf32");
        product.setName("BootCoin");
        product.setType("Pasivo");
        product.setCategory("BootCoin");
        product.setDescription("Moneda Virtual");
        product.setPricePurchase(3.14);
        product.setPriceSale(3.25);

        BootCoinDTO bootCoinDto = new BootCoinDTO();
        bootCoinDto.setId("677ff06ddeea8c5eb02f44f6");
        bootCoinDto.setType("Venta");
        bootCoinDto.setBootCoinAmount(5);
        bootCoinDto.setPaymentMode("Yanki");
        bootCoinDto.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoinDto.setProductId("6789a9f814020f5b783bcf32");
        bootCoinDto.setProduct(product);

        BootCoin bootCoin = new BootCoin();
        bootCoin.setId("677ff06ddeea8c5eb02f44f6");
        bootCoin.setType("Venta");
        bootCoin.setBootCoinAmount(5);
        bootCoin.setPaymentMode("Yanki");
        bootCoin.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoin.setProductId("6789a9f814020f5b783bcf32");

        Mockito.when(bootCoinRepository.findAll()).thenReturn(Flux.just(bootCoin));

        Mockito.when(bootCoinMapper.documentToDto(Mockito.any(BootCoin.class))).thenReturn(bootCoinDto);

        Mockito.when(productRedisService.getProductRedis(Mockito.anyString())).thenReturn(product);

        Flux<BootCoinDTO> result = bootCoinService.getBootCoin("V");

        StepVerifier.create(result)
                .expectNext(bootCoinDto)
                .verifyComplete();

        Mockito.verify(bootCoinRepository).findAll();
    }

    @Test
    void registerBootCoin() {
        BootCoin bootCoin = new BootCoin();
        bootCoin.setId("677ff06ddeea8c5eb02f44f6");
        bootCoin.setType("Venta");
        bootCoin.setBootCoinAmount(5);
        bootCoin.setPaymentMode("Yanki");
        bootCoin.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoin.setProductId("6789a9f814020f5b783bcf32");

        Mockito.when(bootCoinRepository.save(Mockito.any(BootCoin.class))).thenReturn(Mono.just(bootCoin));

        Mono<BootCoin> result = bootCoinService.registerBootCoin(Mono.just(bootCoin));

        StepVerifier.create(result)
                .expectNext(bootCoin)
                .verifyComplete();

        Mockito.verify(bootCoinRepository).save(Mockito.any(BootCoin.class));
    }

    @Test
    void getBootCoinDetail() {
        Product product = new Product();
        product.setId("6789a9f814020f5b783bcf32");
        product.setName("BootCoin");
        product.setType("Pasivo");
        product.setCategory("BootCoin");
        product.setDescription("Moneda Virtual");
        product.setPricePurchase(3.14);
        product.setPriceSale(3.25);

        BootCoinDTO bootCoinDto = new BootCoinDTO();
        bootCoinDto.setId("677ff06ddeea8c5eb02f44f6");
        bootCoinDto.setType("Venta");
        bootCoinDto.setBootCoinAmount(5);
        bootCoinDto.setPaymentMode("Yanki");
        bootCoinDto.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoinDto.setProductId("6789a9f814020f5b783bcf32");
        bootCoinDto.setProduct(product);

        BootCoin bootCoin = new BootCoin();
        bootCoin.setId("677ff06ddeea8c5eb02f44f6");
        bootCoin.setType("Venta");
        bootCoin.setBootCoinAmount(5);
        bootCoin.setPaymentMode("Yanki");
        bootCoin.setWalletId("677ff06ddeea8c5eb02f44f5");
        bootCoin.setProductId("6789a9f814020f5b783bcf32");

        Mockito.when(bootCoinRepository.findById(Mockito.anyString())).thenReturn(Mono.just(bootCoin));

        Mockito.when(bootCoinMapper.documentToDto(Mockito.any(BootCoin.class))).thenReturn(bootCoinDto);

        Mockito.when(productRedisService.getProductRedis(Mockito.anyString())).thenReturn(product);

        Mono<BootCoinDTO> result = bootCoinService.getBootCoinDetail(bootCoin.getId());

        StepVerifier.create(result)
                .expectNext(bootCoinDto)
                .verifyComplete();

        Mockito.verify(bootCoinRepository).findById(Mockito.anyString());
    }

    @Test
    void deleteBootCoin() {
        String id = "6789a9f814020f5b783bcf32";

        Mockito.when(bootCoinRepository.deleteById(Mockito.anyString())).thenReturn(Mono.empty());

        Mono<Void> result = bootCoinService.deleteBootCoin(id);

        StepVerifier.create(result)
                .verifyComplete();

        Mockito.verify(bootCoinRepository).deleteById(id);
    }
}
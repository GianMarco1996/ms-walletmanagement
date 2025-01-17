package com.bootcamp.walletmanagement.service.bootcoin;

import com.bootcamp.walletmanagement.mapper.bootcoin.BootCoinMapper;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoin;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoinDTO;
import com.bootcamp.walletmanagement.repository.bootcoin.BootCoinRepository;
import com.bootcamp.walletmanagement.service.redis.product.ProductRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BootCoinServiceImpl implements BootCoinService{

    @Autowired
    private BootCoinRepository bootCoinRepository;

    @Autowired
    private ProductRedisService productRedisService;

    @Autowired
    private BootCoinMapper bootCoinMapper;

    @Override
    public Flux<BootCoinDTO> getBootCoin(String typeBootCoin) {
        return bootCoinRepository.findAll()
                .map(bootCoinMapper::documentToDto)
                .flatMap(bootCoin -> typeBootCoin.equals("T")
                        ? Flux.just(bootCoin)
                        : typeBootCoin.equals("C")
                            ? Flux.just(bootCoin).filter(b -> b.getType().equals("Compra"))
                            : Flux.just(bootCoin).filter(b -> b.getType().equals("Venta"))
                )
                .map(bootCoin -> {
                    bootCoin.setProduct(productRedisService.getProductRedis(bootCoin.getProductId()));
                    return bootCoin;
                });
    }

    @Override
    public Mono<BootCoin> registerBootCoin(Mono<BootCoin> bootCoin) {
        return bootCoin.flatMap(bootCoinRepository::save);
    }
}
package com.bootcamp.walletmanagement.service.bootcoin;

import com.bootcamp.walletmanagement.model.bootcoin.BootCoin;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoinDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootCoinService {

    Flux<BootCoinDTO> getBootCoin(String typeBootCoin);

    Mono<BootCoin> registerBootCoin(Mono<BootCoin> bootCoin);
}
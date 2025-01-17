package com.bootcamp.walletmanagement.controller.bootcoin;

import com.bootcamp.walletmanagement.api.BootCoinApi;
import com.bootcamp.walletmanagement.mapper.bootcoin.BootCoinMapper;
import com.bootcamp.walletmanagement.model.BootCoinRequest;
import com.bootcamp.walletmanagement.model.BootCoinResponse;
import com.bootcamp.walletmanagement.service.bootcoin.BootCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BootCoinController implements BootCoinApi {

    @Autowired
    private BootCoinService bootCoinService;

    @Autowired
    private BootCoinMapper bootCoinMapper;

    @Override
    public Mono<ResponseEntity<Flux<BootCoinResponse>>> getBootCoin(String typeBootCoin, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok().body(bootCoinService.getBootCoin(typeBootCoin)
                .map(bootCoinMapper::dtoToModel)));
    }

    @Override
    public Mono<ResponseEntity<Object>> registerBootCoin(Mono<BootCoinRequest> bootCoinRequest, ServerWebExchange exchange) {
        return bootCoinService.registerBootCoin(bootCoinRequest.map(bootCoinMapper::modelToDocument))
                .map(ResponseEntity::ok);
    }
}
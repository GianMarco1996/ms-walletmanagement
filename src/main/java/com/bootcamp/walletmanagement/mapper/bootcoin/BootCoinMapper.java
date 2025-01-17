package com.bootcamp.walletmanagement.mapper.bootcoin;

import com.bootcamp.walletmanagement.model.BootCoinRequest;
import com.bootcamp.walletmanagement.model.BootCoinResponse;
import com.bootcamp.walletmanagement.model.BootCoinResponseProduct;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoin;
import com.bootcamp.walletmanagement.model.bootcoin.BootCoinDTO;
import org.springframework.stereotype.Component;

@Component
public class BootCoinMapper {

    public BootCoinDTO documentToDto(BootCoin document) {
        BootCoinDTO bootCoin = new BootCoinDTO();
        bootCoin.setId(document.getId());
        bootCoin.setType(document.getType());
        bootCoin.setBootCoinAmount(document.getBootCoinAmount());
        bootCoin.setPaymentMode(document.getPaymentMode());
        bootCoin.setWalletId(document.getWalletId());
        bootCoin.setProductId(document.getProductId());
        return bootCoin;
    }

    public BootCoinResponse dtoToModel(BootCoinDTO dto) {
        BootCoinResponse bootCoin = new BootCoinResponse();
        bootCoin.setId(dto.getId());
        bootCoin.setType(dto.getType());
        bootCoin.setBootCoinAmount(dto.getBootCoinAmount());
        bootCoin.setPaymentMode(dto.getPaymentMode());
        bootCoin.setWalletId(dto.getWalletId());
        BootCoinResponseProduct product = new BootCoinResponseProduct();
        product.setId(dto.getProduct().getId());
        product.setName(dto.getProduct().getName());
        product.setCategory(dto.getProduct().getCategory());
        if (dto.getType().equals("Compra")) {
            product.setPricePurchase(dto.getProduct().getPricePurchase());
        } else {
            product.setPriceSale(dto.getProduct().getPriceSale());
        }
        bootCoin.setProduct(product);
        return bootCoin;
    }

    public BootCoin modelToDocument(BootCoinRequest model) {
        BootCoin bootCoin = new BootCoin();
        bootCoin.setType(getType(model.getType()));
        bootCoin.setBootCoinAmount(model.getBootCoinAmount());
        bootCoin.setPaymentMode(getPaymentMode(model.getPaymentMode()));
        bootCoin.setWalletId(model.getWalletId());
        bootCoin.setProductId(model.getProductId());
        return bootCoin;
    }

    private String getType(BootCoinRequest.TypeEnum typeEnum) {
        return switch (typeEnum) {
            case C -> "Compra";
            case V -> "Venta";
        };
    }

    private String getPaymentMode(BootCoinRequest.PaymentModeEnum paymentModeEnum) {
        return switch (paymentModeEnum) {
            case Y -> "Yanki";
            case T -> "transferencia";
        };
    }
}
package com.bootcamp.walletmanagement.mapper.wallet;

import com.bootcamp.walletmanagement.model.*;
import com.bootcamp.walletmanagement.model.redis.Transaction;
import com.bootcamp.walletmanagement.model.wallet.Wallet;
import com.bootcamp.walletmanagement.model.wallet.WalletDTO;
import com.bootcamp.walletmanagement.model.wallet.YankearWalletDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WalletMapper {

    public WalletDTO documentToDto(Wallet document) {
        WalletDTO wallet = new WalletDTO();
        wallet.setId(document.getId());
        wallet.setFullNames(document.getFullNames());
        wallet.setDocumentType(document.getDocumentType());
        wallet.setDocumentNumber(document.getDocumentNumber());
        wallet.setMobile(document.getMobile());
        wallet.setMobileImei(document.getMobileImei());
        wallet.setEmail(document.getEmail());
        wallet.setCurrentBalance(document.getCurrentBalance());
        wallet.setDebitCardId(document.getDebitCardId());
        wallet.setStatus(document.getStatus());
        wallet.setBootCoin(document.getBootCoin());
        return wallet;
    }

    public WalletDetailResponse dtoToModel(WalletDTO dto) {
        WalletDetailResponse wallet = new WalletDetailResponse();
        wallet.setId(dto.getId());
        wallet.setFullNames(dto.getFullNames());
        wallet.setDocumentType(dto.getDocumentType());
        wallet.setDocumentNumber(dto.getDocumentNumber());
        wallet.setMobile(dto.getMobile());
        wallet.setMobileImei(dto.getMobileImei());
        wallet.setEmail(dto.getEmail());
        wallet.setCurrentBalance(dto.getCurrentBalance());
        wallet.setDebitCardId(dto.getDebitCardId());
        wallet.setBootCoin(dto.getBootCoin());
        wallet.transactions(dto.getTransactions().stream().map(this::getTransaction).toList());
        return wallet;
    }

    public WalletResponse documentToModel(Wallet document) {
        WalletResponse wallet = new WalletResponse();
        wallet.setId(document.getId());
        wallet.setFullNames(document.getFullNames());
        wallet.setDocumentType(document.getDocumentType());
        wallet.setDocumentNumber(document.getDocumentNumber());
        wallet.setMobile(document.getMobile());
        wallet.setMobileImei(document.getMobileImei());
        wallet.setEmail(document.getEmail());
        wallet.setCurrentBalance(document.getCurrentBalance());
        wallet.setDebitCardId(document.getDebitCardId());
        wallet.setBootCoin(document.getBootCoin());
        return wallet;
    }

    public Wallet modelToDocument(WalletRequest model) {
        Wallet wallet = new Wallet();
        wallet.setFullNames(model.getFullNames());
        wallet.setDocumentType(getDocumentType(model.getDocumentType()));
        wallet.setDocumentNumber(model.getDocumentNumber());
        wallet.setMobile(model.getMobile());
        wallet.setMobileImei(model.getMobileImei());
        wallet.setEmail(model.getEmail());
        wallet.setCurrentBalance(0D);
        wallet.setStatus(Boolean.TRUE);
        return wallet;
    }

    public Wallet updateModelToDocument(WalletRequest model) {
        Wallet wallet = new Wallet();
        wallet.setFullNames(model.getFullNames());
        wallet.setDocumentType(getDocumentType(model.getDocumentType()));
        wallet.setDocumentNumber(model.getDocumentNumber());
        wallet.setMobile(model.getMobile());
        wallet.setMobileImei(model.getMobileImei());
        wallet.setEmail(model.getEmail());
        return wallet;
    }

    public YankearWalletDTO modelToDto(YankearWallet model) {
        YankearWalletDTO yankearWallet = new YankearWalletDTO();
        yankearWallet.setMobileDestination(model.getMobileDestination());
        yankearWallet.setAmount(model.getAmount());
        yankearWallet.setDescription(Objects.nonNull(model.getDescription())
                ? model.getDescription()
                : "El Yanki fue exitoso");
        return yankearWallet;
    }

    private String getDocumentType(WalletRequest.DocumentTypeEnum documentTypeEnum) {
        return switch (documentTypeEnum) {
            case D -> "DNI";
            case CE -> "Carnet de Extranjeria";
            case P -> "Pasaporte";
        };
    }

    private TransactionResponse getTransaction(Transaction dto) {
        TransactionResponse transaction = new TransactionResponse();
        transaction.setId(dto.getId());
        transaction.setCategory(dto.getCategory());
        transaction.setType(dto.getType());
        transaction.setAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());
        transaction.setMobile(dto.getMobile());
        return transaction;
    }
}
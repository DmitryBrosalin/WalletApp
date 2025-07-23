package ru.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.wallet.exception.BadRequestException;
import ru.wallet.model.OperationType;
import ru.wallet.model.dto.RequestDto;
import ru.wallet.model.dto.WalletDto;
import ru.wallet.exception.NotFoundException;
import ru.wallet.mapper.WalletMapper;
import ru.wallet.model.Wallet;
import ru.wallet.repository.WalletRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletDto addNewWallet() {
        UUID uuid = UUID.randomUUID();
        while (walletRepository.existsById(uuid.toString())) {
            uuid = UUID.randomUUID();
        }
        Wallet wallet = new Wallet(uuid.toString(), 0L);
        return WalletMapper.toWalletDto(walletRepository.save(wallet));
    }

    public WalletDto getWallet(String uuid) {
        if (walletRepository.findById(uuid).isEmpty()) {
            throw new NotFoundException("Wallet with UUID = " + uuid + " was not found.");
        } else {
            return WalletMapper.toWalletDto(walletRepository.findById(uuid).get());
        }
    }

    public WalletDto doOperation(RequestDto requestDto) {
        OperationType operationType;
        try {
            operationType = OperationType.valueOf(requestDto.getOperationType());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Incorrect operation type: " + requestDto.getOperationType());
        }
        if (walletRepository.findById(requestDto.getWalletId()).isEmpty()) {
            throw new NotFoundException("Wallet with UUID = " + requestDto.getWalletId() + " was not found.");
        }
        Wallet wallet = walletRepository.findById(requestDto.getWalletId()).get();
        switch (operationType) {
            case DEPOSIT:
                wallet.setBalance(wallet.getBalance() + requestDto.getAmount());
                break;
            case WITHDRAW:
                if (wallet.getBalance() < requestDto.getAmount()) {
                    throw new BadRequestException("Insufficient funds for operation: " + operationType + ", amount = "
                            + requestDto.getAmount());
                } else {
                    wallet.setBalance(wallet.getBalance() - requestDto.getAmount());
                }
                break;
        }
        return WalletMapper.toWalletDto(walletRepository.save(wallet));
    }
}

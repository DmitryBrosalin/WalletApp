package ru.wallet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.wallet.model.dto.RequestDto;
import ru.wallet.model.dto.WalletDto;
import ru.wallet.service.WalletService;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/api/v1/wallet/create-new-wallet")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletDto addNewWallet() {
        return walletService.addNewWallet();
    }

    @PostMapping("/api/v1/wallet")
    public WalletDto doOperation(@RequestBody @Valid RequestDto requestDto) {
        return walletService.doOperation(requestDto);
    }

    @GetMapping("/api/v1/wallets/{walletUUID}")
    public WalletDto getWallet(@PathVariable String walletUUID) {
        return walletService.getWallet(walletUUID);
    }
}

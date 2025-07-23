package ru.wallet.mapper;

import ru.wallet.model.dto.WalletDto;
import ru.wallet.model.Wallet;

public class WalletMapper {
    public static WalletDto toWalletDto(Wallet wallet) {
        return new WalletDto(wallet.getUUID(), wallet.getBalance());
    }
}

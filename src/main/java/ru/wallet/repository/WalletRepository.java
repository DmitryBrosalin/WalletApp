package ru.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.wallet.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
}

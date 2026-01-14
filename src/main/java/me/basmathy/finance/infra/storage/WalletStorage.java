package me.basmathy.finance.infra.storage;

import me.basmathy.finance.core.model.Wallet;

import java.util.Optional;

public interface WalletStorage {
    void saveWallet(String login, Wallet wallet);
    Optional<Wallet> loadWallet(String login);
}
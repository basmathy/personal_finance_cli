package me.basmathy.finance.infra.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.basmathy.finance.core.model.Wallet;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class JsonWalletStorage implements WalletStorage {
    private final ObjectMapper mapper;

    public JsonWalletStorage() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void saveWallet(String login, Wallet wallet) {
        try {
            Files.createDirectories(StoragePaths.DATA_DIR);
            var file = StoragePaths.walletFile(login);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), wallet);
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось сохранить кошелёк: " + e.getMessage());
        }
    }

    @Override
    public Optional<Wallet> loadWallet(String login) {
        var file = StoragePaths.walletFile(login);
        if (!Files.exists(file)) return Optional.empty();
        try {
            return Optional.of(mapper.readValue(file.toFile(), Wallet.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось загрузить кошелёк: " + e.getMessage());
        }
    }
}
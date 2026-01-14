package me.basmathy.finance.infra.storage;

import java.nio.file.Path;

public class StoragePaths {
    public static final Path DATA_DIR = Path.of("data");
    public static Path walletFile(String login) {
        return DATA_DIR.resolve("wallet_" + login + ".json");
    }
}
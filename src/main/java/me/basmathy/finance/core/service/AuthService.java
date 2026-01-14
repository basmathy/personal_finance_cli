package me.basmathy.finance.core.service;

import me.basmathy.finance.cli.CliPrinter;
import me.basmathy.finance.core.model.User;
import me.basmathy.finance.core.model.Wallet;
import me.basmathy.finance.core.util.PasswordHasher;
import me.basmathy.finance.core.util.Validation;
import me.basmathy.finance.infra.repo.UserRepository;
import me.basmathy.finance.infra.storage.WalletStorage;

public class AuthService {
    private final UserRepository users;
    private final WalletStorage storage;
    private final CliPrinter printer;

    private User current;

    public AuthService(UserRepository users, WalletStorage storage, CliPrinter printer) {
        this.users = users;
        this.storage = storage;
        this.printer = printer;
    }

    public void register(String login, String password) {
        Validation.requireNonBlank(login, "Логин пустой.");
        Validation.requireNonBlank(password, "Пароль пустой.");
        if (users.exists(login)) throw new IllegalArgumentException("Пользователь уже существует.");

        String hash = PasswordHasher.sha256(login + ":" + password);
        Wallet wallet = storage.loadWallet(login).orElseGet(Wallet::new);

        users.save(new User(login, hash, wallet));
        printer.println("Пользователь зарегистрирован: " + login);
    }

    public void login(String login, String password) {
        Validation.requireNonBlank(login, "Логин пустой.");
        Validation.requireNonBlank(password, "Пароль пустой.");

        var user = users.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден. register <login> <password>"));

        String hash = PasswordHasher.sha256(login + ":" + password);
        if (!hash.equals(user.passwordHash())) throw new IllegalArgumentException("Неверный пароль.");

        // При входе подтянем файл (если есть) в текущий кошелёк
        var loaded = storage.loadWallet(login).orElse(null);
        if (loaded != null) {
            user.wallet().setCategories(loaded.getCategories());
            user.wallet().setBudgets(loaded.getBudgets());
            user.wallet().setOperations(loaded.getOperations());
        }

        current = user;
        printer.println("Вход выполнен: " + login);
    }

    public void logout() {
        requireAuth();
        storage.saveWallet(current.login(), current.wallet());
        printer.println("Сохранено. Выход из аккаунта: " + current.login());
        current = null;
    }

    public User currentUserOrNull() {
        return current;
    }

    public User requireAuth() {
        if (current == null) throw new IllegalArgumentException("Нужно войти: login <login> <password>");
        return current;
    }
}
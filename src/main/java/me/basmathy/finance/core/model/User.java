package me.basmathy.finance.core.model;

public class User {
    private final String login;
    private final String passwordHash;
    private final Wallet wallet;

    public User(String login, String passwordHash, Wallet wallet) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.wallet = wallet;
    }

    public String login() { return login; }
    public String passwordHash() { return passwordHash; }
    public Wallet wallet() { return wallet; }
}
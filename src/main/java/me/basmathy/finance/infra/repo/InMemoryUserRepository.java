package me.basmathy.finance.infra.repo;

import me.basmathy.finance.core.model.User;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

    @Override
    public void save(User user) {
        users.put(user.login(), user);
    }

    @Override
    public boolean exists(String login) {
        return users.containsKey(login);
    }
}
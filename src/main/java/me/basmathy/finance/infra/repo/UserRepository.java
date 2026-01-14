package me.basmathy.finance.infra.repo;

import me.basmathy.finance.core.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByLogin(String login);
    void save(User user);
    boolean exists(String login);
}
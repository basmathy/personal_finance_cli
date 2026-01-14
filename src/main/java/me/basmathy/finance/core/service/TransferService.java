package me.basmathy.finance.core.service;

import me.basmathy.finance.cli.CliPrinter;
import me.basmathy.finance.core.model.User;
import me.basmathy.finance.core.util.Validation;
import me.basmathy.finance.infra.repo.UserRepository;

public class TransferService {
    private final AuthService auth;
    private final UserRepository users;
    private final WalletService walletService;
    private final CliPrinter printer;

    public TransferService(AuthService auth, UserRepository users, WalletService walletService, CliPrinter printer) {
        this.auth = auth;
        this.users = users;
        this.walletService = walletService;
        this.printer = printer;
    }

    public void transfer(String toLogin, String amountRaw, String note) {
        User from = auth.requireAuth();

        Validation.requireNonBlank(toLogin, "Получатель пустой.");
        double amount = Validation.parsePositiveAmount(amountRaw);

        if (from.login().equals(toLogin)) throw new IllegalArgumentException("Нельзя перевести самому себе.");

        User to = users.findByLogin(toLogin)
                .orElseThrow(() -> new IllegalArgumentException("Получатель не найден: " + toLogin));

        // Фиксируем расход у отправителя (категория "Перевод")
        walletService.addExpense("Переводы", Double.toString(amount), "to=" + toLogin + (note != null ? (", " + note) : ""));

        // Фиксируем доход у получателя (прямо в его кошельке)
        // (без смены текущей сессии: добавим напрямую)
        to.wallet().getCategories().add("Переводы");
        to.wallet().getOperations().add(new me.basmathy.finance.core.model.Operation(
                me.basmathy.finance.core.model.OperationType.INCOME,
                "Переводы",
                amount,
                "from=" + from.login() + (note != null ? (", " + note) : "")
        ));

        printer.println("Перевод выполнен: " + from.login() + " -> " + toLogin + " : " + amount);
    }
}
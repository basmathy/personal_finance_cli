package me.basmathy.finance.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.basmathy.finance.cli.CliPrinter;
import me.basmathy.finance.core.model.Operation;
import me.basmathy.finance.core.model.OperationType;
import me.basmathy.finance.core.model.User;
import me.basmathy.finance.core.util.Validation;
import me.basmathy.finance.infra.repo.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

public class WalletService {
    private final AuthService auth;
    private final UserRepository users;
    private final AlertsService alerts;
    private final CliPrinter printer;

    public WalletService(AuthService auth, UserRepository users, AlertsService alerts, CliPrinter printer) {
        this.auth = auth;
        this.users = users;
        this.alerts = alerts;
        this.printer = printer;
    }

    public void addCategory(String name) {
        User u = auth.requireAuth();
        Validation.requireNonBlank(name, "Название категории пустое.");
        u.wallet().getCategories().add(name);
        printer.println("Категория добавлена: " + name);
    }

    public void setBudget(String category, String amountRaw) {
        User u = auth.requireAuth();
        Validation.requireNonBlank(category, "Категория пустая.");
        double amount = Validation.parsePositiveAmount(amountRaw);

        ensureCategoryExistsOrCreate(u, category);
        u.wallet().getBudgets().put(category, amount);
        printer.println("Бюджет установлен: " + category + " = " + amount);
        alerts.checkAll(u.wallet());
    }

    public void addIncome(String category, String amountRaw, String note) {
        addOperation(OperationType.INCOME, category, amountRaw, note);
    }

    public void addExpense(String category, String amountRaw, String note) {
        addOperation(OperationType.EXPENSE, category, amountRaw, note);
    }

    private void addOperation(OperationType type, String category, String amountRaw, String note) {
        User u = auth.requireAuth();

        Validation.requireNonBlank(category, "Категория пустая.");
        double amount = Validation.parsePositiveAmount(amountRaw);

        ensureCategoryExistsOrCreate(u, category);

        u.wallet().getOperations().add(new Operation(type, category, amount, note));
        printer.println("Операция добавлена: " + type + " / " + category + " / " + amount);
        alerts.checkAll(u.wallet());
    }

    private void ensureCategoryExistsOrCreate(User u, String category) {
        Set<String> cats = u.wallet().getCategories();
        if (!cats.contains(category)) {
            cats.add(category);
            printer.println("⚠ Категория не существовала — создана автоматически: " + category);
        }
    }

    public void exportJson(String path) {
        User u = auth.requireAuth();
        try {
            Path p = Path.of(path);
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            ObjectMapper m = new ObjectMapper().registerModule(new JavaTimeModule());
            m.writerWithDefaultPrettyPrinter().writeValue(p.toFile(), u.wallet());
            printer.println("Экспортировано в JSON: " + path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Экспорт не удался: " + e.getMessage());
        }
    }

    public void importJson(String path) {
        User u = auth.requireAuth();
        try {
            ObjectMapper m = new ObjectMapper().registerModule(new JavaTimeModule());
            var loaded = m.readValue(Path.of(path).toFile(), me.basmathy.finance.core.model.Wallet.class);

            u.wallet().setCategories(loaded.getCategories());
            u.wallet().setBudgets(loaded.getBudgets());
            u.wallet().setOperations(loaded.getOperations());

            printer.println("Импортировано из JSON: " + path);
            alerts.checkAll(u.wallet());
        } catch (IOException e) {
            throw new IllegalArgumentException("Импорт не удался: " + e.getMessage());
        }
    }

    public void exportCsv(String path) {
        User u = auth.requireAuth();
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,category,amount,note,createdAt\n");
        for (var op : u.wallet().getOperations()) {
            sb.append(op.getId()).append(",")
                    .append(op.getType()).append(",")
                    .append(escape(op.getCategory())).append(",")
                    .append(op.getAmount()).append(",")
                    .append(escape(op.getNote())).append(",")
                    .append(op.getCreatedAt())
                    .append("\n");
        }
        printer.writeToFile(path, sb.toString());
        printer.println("Экспортировано в CSV: " + path);
    }

    private String escape(String s) {
        if (s == null) return "";
        String t = s.replace("\"", "\"\"");
        if (t.contains(",") || t.contains("\"") || t.contains("\n")) return "\"" + t + "\"";
        return t;
    }
}

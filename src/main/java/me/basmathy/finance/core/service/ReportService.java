package me.basmathy.finance.core.service;

import me.basmathy.finance.cli.CliPrinter;
import me.basmathy.finance.core.model.Operation;
import me.basmathy.finance.core.model.OperationType;
import me.basmathy.finance.core.model.User;
import me.basmathy.finance.core.util.TimeRange;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final AuthService auth;
    private final CliPrinter printer;

    public ReportService(AuthService auth, CliPrinter printer) {
        this.auth = auth;
        this.printer = printer;
    }

    public void printReport(Map<String, String> options) {
        User u = auth.requireAuth();

        Set<String> filterCats = parseCats(options.get("cats"));
        TimeRange range = new TimeRange(parseDt(options.get("from")), parseDt(options.get("to")));
        String out = options.getOrDefault("out", "terminal");

        List<Operation> ops = u.wallet().getOperations().stream()
                .filter(op -> range.contains(op.getCreatedAt()))
                .collect(Collectors.toList());

        if (!filterCats.isEmpty()) {
            List<String> missing = filterCats.stream()
                    .filter(c -> u.wallet().getCategories().stream().noneMatch(x -> x.equals(c)))
                    .toList();
            if (!missing.isEmpty()) {
                printer.println("⚠ Категории не найдены: " + String.join(", ", missing));
            }
            ops = ops.stream().filter(op -> filterCats.contains(op.getCategory())).toList();
        }

        double totalIncome = sum(ops, OperationType.INCOME);
        double totalExpense = sum(ops, OperationType.EXPENSE);

        Map<String, Double> incomeByCat = sumByCategory(ops, OperationType.INCOME);
        Map<String, Double> expenseByCat = sumByCategory(ops, OperationType.EXPENSE);

        String report = buildReport(u.login(), totalIncome, totalExpense, incomeByCat, expenseByCat, u.wallet().getBudgets(), ops);
        if (out.startsWith("file:")) {
            String path = out.substring("file:".length());
            printer.writeToFile(path, report);
            printer.println("Отчёт записан в файл: " + path);
        } else {
            printer.println(report);
        }
    }

    private String buildReport(String login,
                               double totalIncome,
                               double totalExpense,
                               Map<String, Double> incomeByCat,
                               Map<String, Double> expenseByCat,
                               Map<String, Double> budgets,
                               List<Operation> ops) {

        StringBuilder sb = new StringBuilder();
        sb.append("Пользователь: ").append(login).append("\n");
        sb.append("Операций в отчёте: ").append(ops.size()).append("\n\n");

        sb.append(String.format(Locale.US, "Общий доход: %,.1f%n", totalIncome));
        sb.append("Доходы по категориям:\n");
        if (incomeByCat.isEmpty()) sb.append("  (нет)\n");
        else incomeByCat.forEach((k, v) -> sb.append(String.format(Locale.US, "  %s: %,.1f%n", k, v)));

        sb.append("\n");
        sb.append(String.format(Locale.US, "Общие расходы: %,.1f%n", totalExpense));
        sb.append("Расходы по категориям:\n");
        if (expenseByCat.isEmpty()) sb.append("  (нет)\n");
        else expenseByCat.forEach((k, v) -> sb.append(String.format(Locale.US, "  %s: %,.1f%n", k, v)));

        sb.append("\nБюджет по категориям:\n");
        if (budgets.isEmpty()) {
            sb.append("  (не задан)\n");
        } else {
            // Остаток бюджета считаем по расходам
            for (var e : budgets.entrySet()) {
                String cat = e.getKey();
                double limit = e.getValue();
                double spent = expenseByCat.getOrDefault(cat, 0.0);
                double remaining = limit - spent;
                sb.append(String.format(Locale.US,
                        "  %s: %,.1f, Оставшийся бюджет: %,.1f%n",
                        cat, limit, remaining));
            }
        }
        return sb.toString();
    }

    private double sum(List<Operation> ops, OperationType type) {
        return ops.stream()
                .filter(o -> o.getType() == type)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    private Map<String, Double> sumByCategory(List<Operation> ops, OperationType type) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (var op : ops) {
            if (op.getType() != type) continue;
            map.merge(op.getCategory(), op.getAmount(), Double::sum);
        }
        return map;
    }

    private Set<String> parseCats(String raw) {
        if (raw == null || raw.trim().isEmpty()) return Set.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LocalDateTime parseDt(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDateTime.parse(raw.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректная дата-время. Формат: 2026-01-15T23:59");
        }
    }
}
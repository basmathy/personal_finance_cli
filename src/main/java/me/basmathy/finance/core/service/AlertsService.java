package me.basmathy.finance.core.service;

import me.basmathy.finance.cli.CliPrinter;
import me.basmathy.finance.core.model.OperationType;
import me.basmathy.finance.core.model.Wallet;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlertsService {
    private final CliPrinter printer;

    public AlertsService(CliPrinter printer) {
        this.printer = printer;
    }

    public void checkAll(Wallet wallet) {
        double income = wallet.getOperations().stream()
                .filter(o -> o.getType() == OperationType.INCOME)
                .mapToDouble(o -> o.getAmount()).sum();
        double expense = wallet.getOperations().stream()
                .filter(o -> o.getType() == OperationType.EXPENSE)
                .mapToDouble(o -> o.getAmount()).sum();

        double balance = income - expense;

        if (expense > income) {
            printer.println("⚠ Оповещение: расходы превысили доходы (баланс отрицательный).");
        } else if (balance == 0) {
            printer.println("⚠ Оповещение: баланс равен 0.");
        } else if (balance < 0) {
            printer.println("⚠ Оповещение: баланс < 0.");
        }

        // бюджеты: 80% и превышение
        Map<String, Double> spentByCat = new LinkedHashMap<>();
        wallet.getOperations().forEach(op -> {
            if (op.getType() == OperationType.EXPENSE) {
                spentByCat.merge(op.getCategory(), op.getAmount(), Double::sum);
            }
        });

        for (var e : wallet.getBudgets().entrySet()) {
            String cat = e.getKey();
            double limit = e.getValue();
            double spent = spentByCat.getOrDefault(cat, 0.0);

            if (limit > 0) {
                double ratio = spent / limit;
                if (ratio >= 1.0) {
                    printer.println("⚠ Оповещение: превышен бюджет по категории \"" + cat + "\" (лимит " + limit + ", потрачено " + spent + ").");
                } else if (ratio >= 0.8) {
                    printer.println("ℹ Оповещение: достигнуто 80% бюджета по категории \"" + cat + "\" (лимит " + limit + ", потрачено " + spent + ").");
                }
            }
        }
    }
}
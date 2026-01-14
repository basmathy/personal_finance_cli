package me.basmathy.finance.cli;

import me.basmathy.finance.core.model.User;
import me.basmathy.finance.core.service.AuthService;
import me.basmathy.finance.core.service.ReportService;
import me.basmathy.finance.core.service.TransferService;
import me.basmathy.finance.core.service.WalletService;
import me.basmathy.finance.infra.storage.WalletStorage;

import java.util.Scanner;

public class CommandLoop {
    private final AuthService auth;
    private final WalletService wallet;
    private final ReportService report;
    private final TransferService transfer;
    private final WalletStorage storage;
    private final CliPrinter printer;

    public CommandLoop(AuthService auth,
                       WalletService wallet,
                       ReportService report,
                       TransferService transfer,
                       WalletStorage storage,
                       CliPrinter printer) {
        this.auth = auth;
        this.wallet = wallet;
        this.report = report;
        this.transfer = transfer;
        this.storage = storage;
        this.printer = printer;
    }

    public void run() {
        printer.println("Personal Finance CLI. Введите help для списка команд.");

        Scanner sc = new Scanner(System.in);
        while (true) {
            printer.print("> ");
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            try {
                var cmd = CommandParser.parse(line);

                switch (cmd.name()) {
                    case "help" -> printer.println(CommandsHelp.text());
                    case "register" -> auth.register(cmd.arg(0), cmd.arg(1));
                    case "login" -> auth.login(cmd.arg(0), cmd.arg(1));
                    case "logout" -> auth.logout();
                    case "add-category" -> wallet.addCategory(cmd.arg(0));
                    case "set-budget" -> wallet.setBudget(cmd.arg(0), cmd.arg(1));
                    case "add-income" -> wallet.addIncome(cmd.arg(0), cmd.arg(1), cmd.opt("note"));
                    case "add-expense" -> wallet.addExpense(cmd.arg(0), cmd.arg(1), cmd.opt("note"));
                    case "transfer" -> transfer.transfer(cmd.arg(0), cmd.arg(1), cmd.opt("note"));
                    case "report" -> report.printReport(cmd.options());
                    case "export-json" -> wallet.exportJson(cmd.arg(0));
                    case "import-json" -> wallet.importJson(cmd.arg(0));
                    case "export-csv" -> wallet.exportCsv(cmd.arg(0));
                    case "exit" -> {
                        User u = auth.currentUserOrNull();
                        if (u != null) storage.saveWallet(u.login(), u.wallet());
                        printer.println("Данные сохранены. Выход.");
                        return;
                    }
                    default -> printer.println("Неизвестная команда. help — список команд.");
                }
            } catch (Exception e) {
                printer.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
package me.basmathy.finance;

import me.basmathy.finance.cli.CommandLoop;
import me.basmathy.finance.cli.CliPrinter;
import me.basmathy.finance.core.service.*;
import me.basmathy.finance.infra.repo.*;
import me.basmathy.finance.infra.storage.*;

public class Main {
    public static void main(String[] args) {
        var printer = new CliPrinter();

        UserRepository users = new InMemoryUserRepository();
        WalletStorage storage = new JsonWalletStorage();

        var alerts = new AlertsService(printer);
        var auth = new AuthService(users, storage, printer);
        var wallet = new WalletService(auth, users, alerts, printer);
        var report = new ReportService(auth, printer);
        var transfer = new TransferService(auth, users, wallet, printer);

        new CommandLoop(auth, wallet, report, transfer, storage, printer).run();
    }
}
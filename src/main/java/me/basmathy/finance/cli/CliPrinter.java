package me.basmathy.finance.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CliPrinter {
    public void println(String s) { System.out.println(s); }
    public void print(String s) { System.out.print(s); }

    public void writeToFile(String path, String content) {
        try {
            Path p = Path.of(path);
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.writeString(p, content);
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось записать в файл: " + e.getMessage());
        }
    }
}
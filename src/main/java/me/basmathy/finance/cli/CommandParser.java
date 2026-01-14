package me.basmathy.finance.cli;

import java.util.*;

public class CommandParser {

    public record ParsedCommand(String name, List<String> args, Map<String, String> options) {
        public String arg(int i) {
            if (i < 0 || i >= args.size()) throw new IllegalArgumentException("Не хватает аргументов.");
            return args.get(i);
        }
        public String opt(String key) { return options.get(key); }
    }

    public static ParsedCommand parse(String input) {
        List<String> tokens = tokenize(input);
        String name = tokens.get(0);

        List<String> args = new ArrayList<>();
        Map<String, String> options = new LinkedHashMap<>();

        for (int i = 1; i < tokens.size(); i++) {
            String t = tokens.get(i);
            int eq = t.indexOf('=');
            if (eq > 0) {
                String k = stripQuotes(t.substring(0, eq));
                String v = stripQuotes(t.substring(eq + 1));
                options.put(k, v);
            } else {
                args.add(stripQuotes(t));
            }
        }

        return new ParsedCommand(name, args, options);
    }

    private static List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                cur.append(c);
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (!cur.isEmpty()) {
                    out.add(cur.toString());
                    cur.setLength(0);
                }
            } else {
                cur.append(c);
            }
        }
        if (!cur.isEmpty()) out.add(cur.toString());
        if (out.isEmpty()) throw new IllegalArgumentException("Пустая команда.");
        return out;
    }

    private static String stripQuotes(String s) {
        String t = s.trim();
        if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) {
            return t.substring(1, t.length() - 1);
        }
        return t;
    }
}
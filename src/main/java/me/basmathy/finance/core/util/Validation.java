package me.basmathy.finance.core.util;

public class Validation {
    public static void requireNonBlank(String s, String message) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(message);
    }

    public static double parsePositiveAmount(String raw) {
        requireNonBlank(raw, "Сумма не задана.");
        try {
            double v = Double.parseDouble(raw.replace(',', '.'));
            if (v <= 0) throw new IllegalArgumentException("Сумма должна быть > 0.");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректная сумма: " + raw);
        }
    }
}
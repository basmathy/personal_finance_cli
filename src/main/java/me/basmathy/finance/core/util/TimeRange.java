package me.basmathy.finance.core.util;

import java.time.LocalDateTime;

public record TimeRange(LocalDateTime from, LocalDateTime to) {
    public boolean contains(LocalDateTime t) {
        if (from != null && t.isBefore(from)) return false;
        if (to != null && t.isAfter(to)) return false;
        return true;
    }
}
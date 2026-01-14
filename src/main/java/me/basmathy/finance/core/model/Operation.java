package me.basmathy.finance.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Operation {
    private UUID id;
    private OperationType type;
    private String category;
    private double amount;
    private String note;
    private LocalDateTime createdAt;

    public Operation() { }

    public Operation(OperationType type, String category, double amount, String note) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public OperationType getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setType(OperationType type) { this.type = type; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setNote(String note) { this.note = note; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
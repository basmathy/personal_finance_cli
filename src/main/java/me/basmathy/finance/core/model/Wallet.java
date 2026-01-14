package me.basmathy.finance.core.model;

import java.util.*;

public class Wallet {
    private Set<String> categories = new LinkedHashSet<>();
    private Map<String, Double> budgets = new LinkedHashMap<>();
    private List<Operation> operations = new ArrayList<>();

    public Wallet() { }

    public Set<String> getCategories() { return categories; }
    public Map<String, Double> getBudgets() { return budgets; }
    public List<Operation> getOperations() { return operations; }

    public void setCategories(Set<String> categories) { this.categories = categories; }
    public void setBudgets(Map<String, Double> budgets) { this.budgets = budgets; }
    public void setOperations(List<Operation> operations) { this.operations = operations; }
}
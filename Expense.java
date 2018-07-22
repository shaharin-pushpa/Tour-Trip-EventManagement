package com.example.kowshick.travelmate;

import java.io.Serializable;

public class Expense implements Serializable{
    private String id;
    private String expenseName;
    private double amount;

    public Expense() {
    }

    public Expense(String id, String expenseName, double amount) {
        this.id = id;
        this.expenseName = expenseName;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public double getAmount() {
        return amount;
    }
}

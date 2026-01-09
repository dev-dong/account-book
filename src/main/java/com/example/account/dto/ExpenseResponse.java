package com.example.account.dto;

import com.example.account.domain.Expense;

import java.time.LocalDate;

public record ExpenseResponse(
        String description,
        Long amount,
        String category,
        LocalDate createdAt
) {
    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(expense.getDescription(), expense.getAmount(), expense.getCategory(), expense.getCreatedAt());
    }
}

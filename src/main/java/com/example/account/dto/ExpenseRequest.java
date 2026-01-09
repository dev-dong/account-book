package com.example.account.dto;

public record ExpenseRequest(
        String description,
        Long amount,
        String category
) {
}

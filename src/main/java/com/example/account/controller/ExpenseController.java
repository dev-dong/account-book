package com.example.account.controller;

import com.example.account.domain.Expense;
import com.example.account.dto.ExpenseRequest;
import com.example.account.dto.ExpenseResponse;
import com.example.account.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public Long create(@RequestBody ExpenseRequest request) {
        Expense expense = Expense.create(request.description(), request.amount(), request.category());
        expenseService.saveExpense(expense);
        return expense.getId();
    }

    @GetMapping
    public List<ExpenseResponse> findAll() {
        return expenseService.findAll();
    }
}

package com.example.account.service;

import com.example.account.domain.Expense;
import com.example.account.dto.ExpenseResponse;
import com.example.account.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Transactional
    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    public List<ExpenseResponse> findAll() {
        return expenseRepository.findAll().stream()
                .map(ExpenseResponse::from)
                .toList();
    }
}

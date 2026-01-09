package com.example.account.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Long amount;
    private String category;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    public static Expense create(String description, Long amount, String category) {
        return Expense.builder()
                .description(description)
                .amount(amount)
                .category(category)
                .build();
    }
}

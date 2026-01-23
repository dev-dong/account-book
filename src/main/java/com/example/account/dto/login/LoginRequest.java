package com.example.account.dto.login;

public record LoginRequest(
        String email,
        String password,
        String nickname
) {
}

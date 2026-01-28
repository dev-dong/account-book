package com.example.account.domain.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    GOOGLE("google"),
    KAKAO("kakao"),
    GITHUB("github");

    private final String value;
}

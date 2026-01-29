package com.example.account.domain.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    LOCAL("local"),
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String value;

    public static OAuthProvider of(String registrationId) {
        return Arrays.stream(values())
                .filter(p -> p.value.equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OAuth provider: " + registrationId));
    }
}

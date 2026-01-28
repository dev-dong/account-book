package com.example.account.global.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Slf4j
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attributes;

    public abstract String getProviderId();

    public abstract String getEmail();

    public abstract String getName();

    public abstract String getNameAttributeKey();

    protected String getRequiredAttribute(String key) {
        Object value = attributes.get(key);
        if (value == null) {
            log.error("OAuth2 응답에 필수 속성 '{}'이 없습니다. attributes={}", key, attributes);
            throw new IllegalArgumentException("OAuth2 응답에 필수 속성 '" + key + "'이 없습니다.");
        }
        return value.toString();
    }

    protected String getOptionalAttribute(String key, String defaultValue) {
        Object value = attributes.get(key);
        return value == null ? defaultValue : value.toString();
    }
}

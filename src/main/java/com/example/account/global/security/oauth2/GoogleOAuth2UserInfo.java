package com.example.account.global.security.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    protected GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return getRequiredAttribute("sub");
    }

    @Override
    public String getEmail() {
        return getRequiredAttribute("email");
    }

    @Override
    public String getName() {
        return getOptionalAttribute("name", "google user");
    }

    @Override
    public String getNameAttributeKey() {
        return "sub";
    }
}

package com.example.account.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class OAuth2DebugSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            String registrationId = token.getAuthorizedClientRegistrationId();
            OAuth2User principal = token.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();

            if (principal instanceof OidcUser oidcUser) {
                log.info("[OAUTH2 LOGIN SUCCESS] idToken={}", oidcUser.getIdToken());
            }
        } else {
            log.info("[LOGIN SUCCESS] authenticationType={}", authentication.getClass().getName());
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write("Login Success\n");
    }
}

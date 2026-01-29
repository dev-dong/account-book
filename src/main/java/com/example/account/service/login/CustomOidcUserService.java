package com.example.account.service.login;

import com.example.account.domain.auth.OAuthProvider;
import com.example.account.domain.entity.auth.Member;
import com.example.account.domain.entity.auth.MemberOAuthAccount;
import com.example.account.repository.auth.MemberOAuthAccountRepository;
import com.example.account.repository.auth.MemberRepository;
import com.example.account.repository.auth.MemberRoleGroupRepository;
import com.example.account.repository.auth.RoleGroupsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final MemberRepository memberRepository;
    private final MemberOAuthAccountRepository oauthAccountRepository;
    private final MemberRoleGroupRepository roleRepository;
    private final RoleGroupsRepository roleGroupsRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        /**
         * 1. Member 저장
         * 2. Member OAuth Account 저장
         * 3. Member OAuth Account와 Member를 연관관계 설정
         * 4. Member Roles 조회
         * 5. Roles가 연동되어 있지 않다면 Member roles group에 기본 권한 추가
         */
        // 1. Member 저장
        Map<String, Object> userClaims = userRequest.getIdToken().getClaims();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.of(registrationId);

        String email = userClaims.get("email").toString();
        String providerId = userClaims.get("sub").toString();
        String userName = userClaims.get("name").toString();

        boolean memberExists = memberRepository.findByEmail(email).isPresent();
        if (!memberExists) {
            Member member = Member.createSignup(email, userName);
            memberRepository.save(member);
            MemberOAuthAccount oAuthAccount = member.createOAuthAccount(provider, providerId, email);
            oauthAccountRepository.save(oAuthAccount);
        }
        return super.loadUser(userRequest);
    }
}

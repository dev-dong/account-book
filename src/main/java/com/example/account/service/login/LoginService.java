package com.example.account.service.login;

import com.example.account.domain.auth.OAuthProvider;
import com.example.account.domain.auth.Role;
import com.example.account.domain.entity.auth.*;
import com.example.account.dto.login.LoginRequest;
import com.example.account.repository.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final MemberLocalCredentialRepository localRepository;
    private final MemberOAuthAccountRepository oauthAccountRepository;
    private final MemberRoleGroupRepository roleRepository;
    private final RoleGroupsRepository roleGroupsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String login(LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authenticate = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate.getName();
    }

    @Transactional
    public String localSignup(LoginRequest request) {
        oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.LOCAL, request.email()).ifPresent(a -> {
            throw new IllegalArgumentException("Email already exists");
        });

        RoleGroups roleGroups = roleGroupsRepository.findRoleGroupsByRole(Role.USER).
                orElseThrow(() -> new IllegalStateException("RoleGroups(USER) is empty"));

        Member member = Member.createSignup(request.email(), request.nickname());
        memberRepository.save(member);

        MemberLocalCredential credential = member.createLocalCredential(
                request.email(),
                passwordEncoder.encode(request.password()));
        localRepository.save(credential);

        MemberOAuthAccount oAuthAccount = member.createOAuthAccount(OAuthProvider.LOCAL, request.email(), null);
        oauthAccountRepository.save(oAuthAccount);

        MemberRoleGroup roleGroup = member.createRoleGroup(roleGroups);
        roleRepository.save(roleGroup);

        return member.getEmail();
    }
}

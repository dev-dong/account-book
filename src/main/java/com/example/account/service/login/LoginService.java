package com.example.account.service.login;

import com.example.account.domain.auth.Role;
import com.example.account.domain.entity.auth.Member;
import com.example.account.domain.entity.auth.MemberLocalCredential;
import com.example.account.domain.entity.auth.MemberRoleGroup;
import com.example.account.domain.entity.auth.RoleGroups;
import com.example.account.dto.login.LoginRequest;
import com.example.account.repository.auth.MemberLocalCredentialRepository;
import com.example.account.repository.auth.MemberRepository;
import com.example.account.repository.auth.MemberRoleGroupRepository;
import com.example.account.repository.auth.RoleGroupsRepository;
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
        localRepository.findMemberLocalCredentialByEmail(request.email()).ifPresent(b -> {
            throw new IllegalArgumentException("Email already exists");
        });

        RoleGroups roleGroups = roleGroupsRepository.findRoleGroupsByRole(Role.USER).
                orElseThrow(() -> new IllegalStateException("RoleGroups(USER) is empty"));

        Member member = Member.createForLocalSignup(request.nickname());
        memberRepository.save(member);

        MemberLocalCredential credential = member.createLocalCredential(
                request.email(),
                passwordEncoder.encode(request.password()));
        localRepository.save(credential);

        MemberRoleGroup roleGroup = member.createRoleGroup(roleGroups);
        roleRepository.save(roleGroup);

        return credential.getEmail();
    }
}

package com.example.account.service;

import com.example.account.domain.entity.auth.Member;
import com.example.account.domain.entity.auth.MemberLocalCredential;
import com.example.account.domain.entity.auth.MemberRoleGroup;
import com.example.account.repository.auth.MemberLocalCredentialRepository;
import com.example.account.repository.auth.MemberRoleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberLocalCredentialRepository localRepository;
    private final MemberRoleGroupRepository roleGroupRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberLocalCredential user = localRepository
                .findMemberLocalCredentialByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        Member member = user.getMember();
        List<MemberRoleGroup> roleGroups = roleGroupRepository.findByMember(member);
        List<SimpleGrantedAuthority> authorities = roleGroups.stream()
                .map(rg -> new SimpleGrantedAuthority("ROLE" + rg.getRoleGroup().getRole().name()))
                .toList();

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}

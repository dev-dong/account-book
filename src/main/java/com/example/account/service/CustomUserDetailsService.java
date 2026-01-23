package com.example.account.service;

import com.example.account.domain.entity.auth.MemberLocalCredential;
import com.example.account.repository.auth.MemberLocalCredentialRepository;
import com.example.account.repository.auth.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberLocalCredentialRepository localRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberLocalCredential user = localRepository.findMemberLocalCredentialByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}

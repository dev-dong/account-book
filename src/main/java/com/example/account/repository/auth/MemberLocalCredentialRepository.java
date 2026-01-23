package com.example.account.repository.auth;

import com.example.account.domain.entity.auth.MemberLocalCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberLocalCredentialRepository extends JpaRepository<MemberLocalCredential, Long> {
    Optional<MemberLocalCredential> findMemberLocalCredentialByEmail(String email);
}
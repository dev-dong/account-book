package com.example.account.repository.auth;

import com.example.account.domain.entity.auth.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

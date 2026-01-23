package com.example.account.repository.auth;

import com.example.account.domain.entity.auth.MemberRoleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleGroupRepository extends JpaRepository<MemberRoleGroup, Long> {
}
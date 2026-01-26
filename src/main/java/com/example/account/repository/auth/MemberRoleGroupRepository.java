package com.example.account.repository.auth;

import com.example.account.domain.entity.auth.Member;
import com.example.account.domain.entity.auth.MemberRoleGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRoleGroupRepository extends JpaRepository<MemberRoleGroup, Long> {

    @EntityGraph(attributePaths = "roleGroup")
    List<MemberRoleGroup> findByMember(Member member);
}
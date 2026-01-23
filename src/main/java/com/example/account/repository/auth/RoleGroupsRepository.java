package com.example.account.repository.auth;

import com.example.account.domain.auth.Role;
import com.example.account.domain.entity.auth.RoleGroups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleGroupsRepository extends JpaRepository<RoleGroups, Long> {
    Optional<RoleGroups> findRoleGroupsByRole(Role role);
}
package com.example.account.domain.entity.auth;

import com.example.account.domain.auth.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_groups",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_role_groups_name", columnNames = "group_name")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoleGroups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 50)
    private String groupName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;
}

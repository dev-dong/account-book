package com.example.account.domain.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "member_role_groups",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_member_group", columnNames = {"member_id", "role_group_id"})
        },
        indexes = {
                @Index(name = "idx_mrg_member_id", columnList = "member_id"),
                @Index(name = "idx_mrg_role_id", columnList = "role_group_id")
        }
)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberRoleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_group_id", nullable = false)
    private RoleGroups roleGroup;
}

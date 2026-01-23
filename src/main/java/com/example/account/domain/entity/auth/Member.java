package com.example.account.domain.entity.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    void prePersist() {
        if (joinedAt == null) joinedAt = Instant.now();
    }

    // ===== Static Factory Method ===== //
    public static Member createForLocalSignup(String nickname) {
        return Member.builder()
                .nickname(nickname)
                .build();
    }

    public MemberLocalCredential createLocalCredential(String email, String password) {
        if (this.id == null) {
            throw new IllegalStateException("Member must be saved before creating credentials");
        }

        return MemberLocalCredential.builder()
                .member(this)
                .email(email)
                .password(password)
                .build();
    }

    public MemberRoleGroup createRoleGroup(RoleGroups roleGroups) {
        if (this.id == null) {
            throw new IllegalStateException("Member must be saved before creating credentials");
        }
        
        return MemberRoleGroup.builder()
                .member(this)
                .roleGroup(roleGroups)
                .build();
    }
}

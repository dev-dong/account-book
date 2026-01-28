package com.example.account.domain.entity.auth;

import com.example.account.domain.auth.OAuthProvider;
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

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    void prePersist() {
        if (joinedAt == null) joinedAt = Instant.now();
    }

    // ===== Static Factory Method ===== //
    public static Member createSignup(String email, String nickname) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }

    public MemberLocalCredential createLocalCredential(String password) {
        if (this.id == null) {
            throw new IllegalStateException("Member must be saved before creating credentials");
        }

        return MemberLocalCredential.builder()
                .member(this)
                .password(password)
                .build();
    }

    public MemberOAuthAccount createOAuthAccount(OAuthProvider provider, String providerUserId, String email) {
        if (this.id == null) {
            throw new IllegalStateException("Member must be saved before creating credentials");
        }

        return MemberOAuthAccount.builder()
                .member(this)
                .provider(provider)
                .providerUserId(providerUserId)
                .email(email)
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

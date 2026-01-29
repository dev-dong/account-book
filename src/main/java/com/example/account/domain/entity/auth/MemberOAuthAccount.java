package com.example.account.domain.entity.auth;

import com.example.account.domain.auth.OAuthProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(
        name = "member_oauth_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_moa_provider_user",
                        columnNames = {"provider", "provider_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_moa_member_id", columnList = "member_id")
        }

)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class MemberOAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_moa_member")
    )
    private Member member;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(name = "provider_email", length = 100)
    private String providerEmail;

    public void updateEmail(String email) {
        this.providerEmail = email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MemberOAuthAccount that)) return false;
        return provider == that.provider && providerUserId.equals(that.providerUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, providerUserId);
    }
}

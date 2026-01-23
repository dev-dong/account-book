package com.example.account.domain.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "member_local_credential",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_mlc_member_id", columnNames = "member_id"),
                @UniqueConstraint(name = "uq_mlc_email", columnNames = "email")
        }
)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberLocalCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}

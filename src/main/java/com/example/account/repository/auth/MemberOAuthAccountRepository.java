package com.example.account.repository.auth;

import com.example.account.domain.auth.OAuthProvider;
import com.example.account.domain.entity.auth.MemberOAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberOAuthAccountRepository extends JpaRepository<MemberOAuthAccount, Long> {
    Optional<MemberOAuthAccount> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    @Query("select moa from MemberOAuthAccount moa " +
            "join fetch moa.member " +
            "where moa.provider = :provider and moa.providerUserId = :providerUserId")
    Optional<MemberOAuthAccount> findByProviderAndProviderUserIdWithMember(
            @Param("provider") OAuthProvider provider,
            @Param("providerUserId") String providerUserId);

    boolean existsByMemberIdAndProvider(Long memberId, OAuthProvider provider);
}
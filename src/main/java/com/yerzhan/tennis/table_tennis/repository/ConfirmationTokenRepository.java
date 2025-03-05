package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.model.ConfirmationToken;
import com.yerzhan.tennis.table_tennis.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
    Optional<ConfirmationToken> findByUserIdAndTokenType(Long userId, TokenType tokenType);
} 
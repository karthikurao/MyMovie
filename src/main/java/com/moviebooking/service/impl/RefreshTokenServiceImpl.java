package com.moviebooking.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moviebooking.entity.RefreshToken;
import com.moviebooking.exception.RefreshTokenException;
import com.moviebooking.repository.IRefreshTokenRepository;
import com.moviebooking.service.IRefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private static final int TOKEN_BYTE_LENGTH = 64;

    private final IRefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenValidityMs;

    public RefreshTokenServiceImpl(IRefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public RefreshToken createToken(String subject, String role) {
        if (subject == null || subject.isBlank()) {
            throw new RefreshTokenException("Subject is required to create refresh token");
        }
        if (role == null || role.isBlank()) {
            throw new RefreshTokenException("Role is required to create refresh token");
        }

        RefreshToken token = buildToken(subject, role);
        return refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public RefreshToken rotateToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            throw new RefreshTokenException("Refresh token is required");
        }

        RefreshToken existing = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        Instant now = Instant.now();

        if (existing.isRevoked()) {
            throw new RefreshTokenException("Refresh token has been revoked");
        }

        if (existing.getExpiresAt().isBefore(now)) {
            existing.setRevoked(true);
            existing.setRevokedAt(now);
            refreshTokenRepository.save(existing);
            throw new RefreshTokenException("Refresh token expired");
        }

        RefreshToken replacement = buildToken(existing.getSubject(), existing.getRole());
        refreshTokenRepository.save(replacement);

        existing.setRevoked(true);
        existing.setRevokedAt(now);
        existing.setReplacedByToken(replacement.getToken());
        refreshTokenRepository.save(existing);

        return replacement;
    }

    @Override
    @Transactional
    public void revokeTokensForSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            return;
        }
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllBySubjectAndRevokedFalse(subject);
        if (activeTokens.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        activeTokens.forEach(token -> {
            token.setRevoked(true);
            token.setRevokedAt(now);
        });

        refreshTokenRepository.saveAll(activeTokens);
    }

    @Override
    @Transactional
    public void revokeToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return;
        }
        refreshTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    @Override
    public long getRefreshTokenValidityMillis() {
        return refreshTokenValidityMs;
    }

    private RefreshToken buildToken(String subject, String role) {
        Instant now = Instant.now();
        RefreshToken token = new RefreshToken();
        token.setSubject(subject);
        token.setRole(role);
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMillis(refreshTokenValidityMs));
        token.setToken(generateToken());
        token.setRevoked(false);
        token.setRevokedAt(null);
        token.setReplacedByToken(null);
        return token;
    }

    private String generateToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

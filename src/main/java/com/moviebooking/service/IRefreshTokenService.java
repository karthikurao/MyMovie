package com.moviebooking.service;

import com.moviebooking.entity.RefreshToken;

public interface IRefreshTokenService {

    RefreshToken createToken(String subject, String role);

    RefreshToken rotateToken(String tokenValue);

    void revokeTokensForSubject(String subject);

    void revokeToken(String tokenValue);

    long getRefreshTokenValidityMillis();
}

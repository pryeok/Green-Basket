package com.greenbasket.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private final String secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtUtil(String secretKey, long accessExpirationTime, long refreshExpirationTime) {
        this.secretKey = secretKey;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성 (30분)
    public String generateAccessToken(String userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성 (7일)
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 userId 추출
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // 토큰에서 role 추출
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    // 토큰 타입 확인 (access or refresh)
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("type", String.class);
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT claims string is empty");
        }
    }

    // Access Token인지 확인
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    // Refresh Token인지 확인
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }
}

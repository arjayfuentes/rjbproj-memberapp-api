package com.rjproj.memberapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JWTUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationTime;


    private String token;

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        if(claims.get("permissions") != null) {
            return (List<String>) claims.get("permissions");
        }
        return null;

    }

    public UUID extractSelectedOrganizationId(String token) {
        Claims claims = extractAllClaims(token);
        if(claims.get("selectedOrganizationId") != null) {
            return UUID.fromString((String) claims.get("selectedOrganizationId"));
        }
        return null;
    }

    public UUID extractMemberId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString((String) claims.get("memberId"));
    }

    public UUID extractMemberIdInternally() {
        Claims claims = extractAllClaims(this.token);
        return UUID.fromString((String) claims.get("memberId"));
    }

    public String getToken() {
        return token;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}

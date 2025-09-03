package com.hotel.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.access-token-expiration}")
    private int accessTokenExpiration;
    
    @Value("${app.jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;
    
    @Value("${app.jwt.issuer}")
    private String issuer;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generate access token from authentication
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateAccessToken(userPrincipal);
    }
    
    /**
     * Generate access token from UserDetails
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("type", "access");
        
        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        
        return createToken(claims, userDetails.getUsername(), refreshTokenExpiration);
    }
    
    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, int expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000L);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (java.util.List<String>) claims.get("roles");
    }
    
    /**
     * Extract token type
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("type");
    }
    
    /**
     * Extract specific claim from token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    /**
     * Validate token
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate token structure and signature
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if token is access token
     */
    public Boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if token is refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get token expiration as LocalDateTime
     */
    public LocalDateTime getTokenExpirationAsLocalDateTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }
    
    /**
     * Get access token expiration time in seconds
     */
    public int getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    /**
     * Get refresh token expiration time in seconds
     */
    public int getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}

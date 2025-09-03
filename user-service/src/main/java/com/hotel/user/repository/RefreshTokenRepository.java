package com.hotel.user.repository;

import com.hotel.user.entity.RefreshToken;
import com.hotel.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Find refresh token by token string
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find all valid refresh tokens for a user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Find all refresh tokens for a user
     */
    List<RefreshToken> findByUser(User user);
    
    /**
     * Find expired tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiresAt < :now")
    List<RefreshToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Revoke all tokens for a user
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllUserTokens(@Param("user") User user);
    
    /**
     * Revoke token by token string
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    void revokeToken(@Param("token") String token);
    
    /**
     * Delete expired tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Count valid tokens for a user
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    Long countValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Find tokens by device info
     */
    List<RefreshToken> findByDeviceInfo(String deviceInfo);
    
    /**
     * Find tokens by IP address
     */
    List<RefreshToken> findByIpAddress(String ipAddress);
}

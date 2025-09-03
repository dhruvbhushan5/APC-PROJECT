package com.hotel.user.repository;

import com.hotel.user.entity.User;
import com.hotel.common.dto.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by email verification token
     */
    Optional<User> findByEmailVerificationToken(String token);
    
    /**
     * Find user by password reset token
     */
    Optional<User> findByPasswordResetToken(String token);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find enabled users
     */
    List<User> findByEnabledTrue();
    
    /**
     * Find users by email verification status
     */
    List<User> findByEmailVerified(Boolean emailVerified);
    
    /**
     * Find users with expired email verification tokens
     */
    @Query("SELECT u FROM User u WHERE u.emailVerificationTokenExpiresAt < :now")
    List<User> findUsersWithExpiredEmailVerificationTokens(@Param("now") LocalDateTime now);
    
    /**
     * Find users with expired password reset tokens
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetTokenExpiresAt < :now")
    List<User> findUsersWithExpiredPasswordResetTokens(@Param("now") LocalDateTime now);
    
    /**
     * Find users by role
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roleAssignments ra WHERE ra.role = :role AND ra.active = true")
    List<User> findByRole(@Param("role") UserRole role);
    
    /**
     * Find users created within date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Page<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate, 
                                      Pageable pageable);
    
    /**
     * Find users who logged in within date range
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt BETWEEN :startDate AND :endDate")
    List<User> findUsersLoggedInBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count users by enabled status
     */
    Long countByEnabled(Boolean enabled);
    
    /**
     * Count users by email verification status
     */
    Long countByEmailVerified(Boolean emailVerified);
    
    /**
     * Count users by role
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roleAssignments ra WHERE ra.role = :role AND ra.active = true")
    Long countByRole(@Param("role") UserRole role);
    
    /**
     * Find users by first name or last name containing search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find users by multiple criteria
     */
    @Query("""
        SELECT DISTINCT u FROM User u 
        LEFT JOIN u.roleAssignments ra 
        WHERE (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
        AND (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
        AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
        AND (:enabled IS NULL OR u.enabled = :enabled)
        AND (:emailVerified IS NULL OR u.emailVerified = :emailVerified)
        AND (:role IS NULL OR (ra.role = :role AND ra.active = true))
        """)
    Page<User> findUsersByCriteria(@Param("email") String email,
                                  @Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("enabled") Boolean enabled,
                                  @Param("emailVerified") Boolean emailVerified,
                                  @Param("role") UserRole role,
                                  Pageable pageable);
}

package com.hotel.user.entity;

import com.hotel.common.dto.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_email_verification_token", columnList = "emailVerificationToken"),
        @Index(name = "idx_users_password_reset_token", columnList = "passwordResetToken")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"roleAssignments"})
@ToString(exclude = {"roleAssignments"})
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 200)
    private String address;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean accountNonExpired = true;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean accountNonLocked = true;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;
    
    @Column
    private LocalDateTime lastLoginAt;
    
    @Column
    private String emailVerificationToken;
    
    @Column
    private LocalDateTime emailVerificationTokenExpiresAt;
    
    @Column
    private String passwordResetToken;
    
    @Column
    private LocalDateTime passwordResetTokenExpiresAt;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRoleAssignment> roleAssignments;
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleAssignments.stream()
                .map(assignment -> new SimpleGrantedAuthority(assignment.getRole().getAuthority()))
                .collect(Collectors.toSet());
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Set<UserRole> getRoles() {
        return roleAssignments.stream()
                .map(UserRoleAssignment::getRole)
                .collect(Collectors.toSet());
    }
}

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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_role_assignments", 
       indexes = {
               @Index(name = "idx_user_role_assignments_user_id", columnList = "user_id"),
               @Index(name = "idx_user_role_assignments_role", columnList = "role")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class UserRoleAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(length = 100)
    private String assignedBy;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}

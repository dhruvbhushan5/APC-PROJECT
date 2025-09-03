package com.hotel.roomservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items", indexes = {
    @Index(name = "idx_menu_category", columnList = "category"),
    @Index(name = "idx_menu_available", columnList = "available"),
    @Index(name = "idx_menu_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuCategory category;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 255)
    private String imageUrl;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(length = 50)
    private String preparationTime; // e.g., "15-20 minutes"
    
    @Column(length = 1000)
    private String ingredients;
    
    @Column(length = 200)
    private String allergens;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean vegetarian = false;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean vegan = false;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean glutenFree = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum MenuCategory {
        APPETIZERS,
        MAIN_COURSE,
        DESSERTS,
        BEVERAGES,
        ALCOHOLIC_BEVERAGES,
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACKS,
        COFFEE_TEA
    }
}

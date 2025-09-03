package com.hotel.roomservice.repository;

import com.hotel.roomservice.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByAvailableTrue();
    
    List<MenuItem> findByCategoryAndAvailableTrue(MenuItem.MenuCategory category);
    
    List<MenuItem> findByNameContainingIgnoreCaseAndAvailableTrue(String name);
    
    @Query("SELECT m FROM MenuItem m WHERE m.vegetarian = true AND m.available = true")
    List<MenuItem> findVegetarianItems();
    
    @Query("SELECT m FROM MenuItem m WHERE m.vegan = true AND m.available = true")
    List<MenuItem> findVeganItems();
    
    @Query("SELECT m FROM MenuItem m WHERE m.glutenFree = true AND m.available = true")
    List<MenuItem> findGlutenFreeItems();
    
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.available = true ORDER BY m.category")
    List<MenuItem.MenuCategory> findAvailableCategories();
}

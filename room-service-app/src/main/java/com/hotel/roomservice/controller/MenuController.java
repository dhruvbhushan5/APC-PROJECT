package com.hotel.roomservice.controller;

import com.hotel.roomservice.entity.MenuItem;
import com.hotel.roomservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MenuController {
    
    private final MenuItemRepository menuItemRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMenuItems() {
        log.info("Fetching all available menu items");
        
        try {
            List<MenuItem> menuItems = menuItemRepository.findByAvailableTrue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", menuItems);
            response.put("message", "Menu items retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu items: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch menu items: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getMenuItemsByCategory(@PathVariable MenuItem.MenuCategory category) {
        log.info("Fetching menu items for category: {}", category);
        
        try {
            List<MenuItem> menuItems = menuItemRepository.findByCategoryAndAvailableTrue(category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", menuItems);
            response.put("message", "Menu items for category " + category + " retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu items for category {}: {}", category, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch menu items for category: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMenuItems(@RequestParam String query) {
        log.info("Searching menu items with query: {}", query);
        
        try {
            List<MenuItem> menuItems = menuItemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(query);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", menuItems);
            response.put("message", "Search completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching menu items: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Search failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAvailableCategories() {
        log.info("Fetching available menu categories");
        
        try {
            List<MenuItem.MenuCategory> categories = menuItemRepository.findAvailableCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            response.put("message", "Categories retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch categories: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/dietary")
    public ResponseEntity<Map<String, Object>> getDietaryOptions(
            @RequestParam(required = false, defaultValue = "false") boolean vegetarian,
            @RequestParam(required = false, defaultValue = "false") boolean vegan,
            @RequestParam(required = false, defaultValue = "false") boolean glutenFree) {
        
        log.info("Fetching dietary options - vegetarian: {}, vegan: {}, glutenFree: {}", vegetarian, vegan, glutenFree);
        
        try {
            List<MenuItem> menuItems;
            
            if (vegan) {
                menuItems = menuItemRepository.findVeganItems();
            } else if (vegetarian) {
                menuItems = menuItemRepository.findVegetarianItems();
            } else if (glutenFree) {
                menuItems = menuItemRepository.findGlutenFreeItems();
            } else {
                menuItems = menuItemRepository.findByAvailableTrue();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", menuItems);
            response.put("message", "Dietary options retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching dietary options: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch dietary options: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMenuItem(@PathVariable Long id) {
        log.info("Fetching menu item with ID: {}", id);
        
        try {
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", menuItem);
            response.put("message", "Menu item retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu item {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch menu item: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

package org.example.catalogservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "menu_items")
public class MenuItem {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int price;

    public MenuItem(String name, int price) {
        if (name == null || name.isEmpty() || price <= 0) {
            throw new IllegalArgumentException("name cannot be null or empty and price cannot be less than or equal to 0");
        }
        this.name = name;
        this.price = price;
    }

    public MenuItem() {}
}

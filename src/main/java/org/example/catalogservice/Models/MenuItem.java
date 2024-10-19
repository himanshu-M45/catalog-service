package org.example.catalogservice.Models;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.catalogservice.Exceptions.CannotCreateMenuItemException;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "menu_items", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Getter
public class MenuItem {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int price;

    public MenuItem(String name, int price) {
        if (name == null || name.isEmpty() || price <= 0) {
            throw new CannotCreateMenuItemException("name cannot be null or empty and price cannot be less than or equal to 0");
        }
        this.name = name;
        this.price = price;
    }

    public MenuItem() {
    }
}

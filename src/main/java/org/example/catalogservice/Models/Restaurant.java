package org.example.catalogservice.Models;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.catalogservice.Exceptions.CannotCreateRestaurantException;

import java.util.List;

@Entity
@Table(name = "restaurants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "address"})
})
@Getter
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String address;

    @ManyToMany
    @JoinTable(
            name = "restaurant_menu_items",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<MenuItem> menu;

    public Restaurant(String name, String address) {
        if (name == null || name.isEmpty() || address == null || address.isEmpty()) {
            throw new CannotCreateRestaurantException("name and address cannot be null or empty");
        }
        this.name = name;
        this.address = address;
    }

    public Restaurant() {
    }
}

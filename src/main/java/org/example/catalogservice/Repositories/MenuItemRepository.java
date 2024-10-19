package org.example.catalogservice.Repositories;

import org.example.catalogservice.Models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
}

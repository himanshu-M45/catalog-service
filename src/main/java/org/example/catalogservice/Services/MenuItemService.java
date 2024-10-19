package org.example.catalogservice.Services;

import org.example.catalogservice.Exceptions.MenuItemAlreadyAddedException;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Repositories.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;

    public String addMenuItem(String name, int price) {
        try {
            menuItemRepository.save(new MenuItem(name, price));
            return "menu item added successfully";
        } catch (DataIntegrityViolationException e) {
            throw new MenuItemAlreadyAddedException("menu item details already added");
        }
    }
}

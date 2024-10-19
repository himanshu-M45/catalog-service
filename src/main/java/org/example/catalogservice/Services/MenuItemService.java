package org.example.catalogservice.Services;

import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.MenuItemAlreadyAddedException;
import org.example.catalogservice.Exceptions.MenuItemDoesNotExistException;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Repositories.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;

    public String addMenuItem(String name, int price) {
        try {
            menuItemRepository.save(new MenuItem(name, price));
            return "menu item added successfully";
        } catch (DataIntegrityViolationException e) {
            throw new MenuItemAlreadyAddedException("menu item already added");
        }
    }

    public MenuItem findById(Integer id) {
        MenuItem menuItem = menuItemRepository.findById(id).orElse(null);
        if (menuItem != null) {
            return menuItem;
        }
        throw new MenuItemDoesNotExistException("menu item does not exist");
    }

    public List<MenuItem> findAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        if (menuItems.isEmpty()) {
            throw new MenuItemDoesNotExistException("no menu items found");
        }
        return menuItems;
    }

    public GETResponseDTO convertToDto(MenuItem menuItem) {
        GETResponseDTO dto = new GETResponseDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setPrice(Optional.of(menuItem.getPrice()));
        return dto;
    }

    public List<MenuItem> findAllById(List<Integer> menuItemIdList) {
        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIdList);
        if (menuItems.size() != menuItemIdList.size()) {
            throw new MenuItemDoesNotExistException("one or more menu items do not exist");
        }
        return menuItems;
    }
}

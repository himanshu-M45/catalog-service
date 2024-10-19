package org.example.catalogservice.Services;

import org.example.catalogservice.Exceptions.CannotCreateMenuItemException;
import org.example.catalogservice.Exceptions.MenuItemAlreadyAddedException;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Repositories.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class MenuItemServiceTest {
    
    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMenuItem() {
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(new MenuItem());

        String response = menuItemService.addMenuItem("Margherita Pizza", 80);

        assertEquals("menu item added successfully", response);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testAddMenuItemWithInvalidName() {
        Exception exception = assertThrows(CannotCreateMenuItemException.class, () -> {
            menuItemService.addMenuItem("", 10);
        });

        assertEquals("name cannot be null or empty and price cannot be less than or equal to 0", exception.getMessage());
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void testAddMenuItemWithInvalidPrice() {
        Exception exception = assertThrows(CannotCreateMenuItemException.class, () -> {
            menuItemService.addMenuItem("", 0);
        });

        assertEquals("name cannot be null or empty and price cannot be less than or equal to 0", exception.getMessage());
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void testAddDuplicateMenuItemThrowsException() {
        when(menuItemRepository.save(any(MenuItem.class)))
            .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        Exception exception = assertThrows(MenuItemAlreadyAddedException.class, () -> {
            menuItemService.addMenuItem("Margherita Pizza", 80);
        });

        assertEquals("menu item details already added", exception.getMessage());
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

}
package org.example.catalogservice.Services;

import org.example.catalogservice.Exceptions.*;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Repositories.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        assertEquals("menu item already added", exception.getMessage());
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testFindByIdSuccess() {
        when(menuItemRepository.findById(anyInt())).thenReturn(Optional.of(new MenuItem()));

        MenuItem retrievedMenuItem = menuItemService.findById(1);

        assertNotNull(retrievedMenuItem);
        verify(menuItemRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        when(menuItemRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(MenuItemDoesNotExistException.class, () -> {
            menuItemService.findById(420);
        });

        assertEquals("menu item does not exist", exception.getMessage());
    }

    @Test
    void testFindAllMenuItemsSuccess() {
        MenuItem menuItem = new MenuItem("Margherita Pizza", 80);
        when(menuItemRepository.findAll()).thenReturn(List.of(menuItem));

        List<MenuItem> menuItems = menuItemService.findAllMenuItems();

        assertNotNull(menuItems);
        assertEquals(1, menuItems.size());
        assertEquals("Margherita Pizza", menuItems.get(0).getName());
        verify(menuItemRepository, times(1)).findAll();
    }

    @Test
    void testFindAllMenuItemsNotFound() {
        when(menuItemRepository.findAll()).thenReturn(List.of());

        Exception exception = assertThrows(MenuItemDoesNotExistException.class, () -> {
            menuItemService.findAllMenuItems();
        });

        assertEquals("no menu items found", exception.getMessage());
        verify(menuItemRepository, times(1)).findAll();
    }

    @Test
    void testFindAllByIdSuccess() {
        List<Integer> menuItemIds = List.of(1, 2);
        MenuItem menuItem1 = new MenuItem("Pizza", 100);
        MenuItem menuItem2 = new MenuItem("Burger", 50);

        when(menuItemRepository.findAllById(menuItemIds)).thenReturn(List.of(menuItem1, menuItem2));

        List<MenuItem> menuItems = menuItemService.findAllById(menuItemIds);

        assertNotNull(menuItems);
        assertEquals(2, menuItems.size());
        assertEquals("Pizza", menuItems.get(0).getName());
        assertEquals("Burger", menuItems.get(1).getName());
        verify(menuItemRepository, times(1)).findAllById(menuItemIds);
    }

    @Test
    void testFindAllByIdMenuItemNotFound() {
        List<Integer> menuItemIds = List.of(1, 2);

        when(menuItemRepository.findAllById(menuItemIds)).thenReturn(List.of());

        Exception exception = assertThrows(MenuItemDoesNotExistException.class, () -> {
            menuItemService.findAllById(menuItemIds);
        });

        assertEquals("one or more menu items do not exist", exception.getMessage());
        verify(menuItemRepository, times(1)).findAllById(menuItemIds);
    }
}
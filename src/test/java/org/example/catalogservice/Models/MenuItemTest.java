package org.example.catalogservice.Models;

import org.example.catalogservice.Exceptions.CannotCreateMenuItemException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void testMenuItemCreation() {
        assertDoesNotThrow(() -> {
            new MenuItem("Burger", 10);
        });
    }

    @Test
    void testMenuItemCreationWithInvalidName() {
        assertThrows(CannotCreateMenuItemException.class, () -> {
            new MenuItem("", 10);
        });
    }

    @Test
    void testMenuItemCreationWithInvalidPrice() {
        assertThrows(CannotCreateMenuItemException.class, () -> {
            new MenuItem("Burger", -1);
        });
    }
}
package org.example.catalogservice.Models;

import org.example.catalogservice.Exceptions.CannotCreateRestaurantException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void testRestaurantCreation() {
        assertDoesNotThrow(() -> {
            new Restaurant("Pizza Place", "123 Main St");
        });
    }

    @Test
    void testRestaurantCreationWithInvalidName() {
        assertThrows(CannotCreateRestaurantException.class, () -> {
            new Restaurant("", "123 Main St");
        });
    }

    @Test
    void testRestaurantCreationWithInvalidAddress() {
        assertThrows(CannotCreateRestaurantException.class, () -> {
            new Restaurant("Pizza Place", "");
        });
    }
}
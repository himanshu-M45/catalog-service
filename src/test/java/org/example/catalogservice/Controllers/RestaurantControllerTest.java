package org.example.catalogservice.Controllers;

import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.*;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Services.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestaurantControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    void testAddRestaurantSuccess() throws Exception {
        when(restaurantService.addRestaurant(anyString(), anyString())).thenReturn("restaurant added successfully");

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pizza Place\", \"address\": \"123 Main St\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data").value("restaurant added successfully"));

        verify(restaurantService, times(1)).addRestaurant("Pizza Place", "123 Main St");
    }

    @Test
    void testAddAlreadyAddedRestaurantBadRequestThrowsException() throws Exception {
        when(restaurantService.addRestaurant(anyString(), anyString()))
                .thenThrow(new RestaurantDetailsAlreadyAddedException("restaurant details already added"));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pizza Place\", \"address\": \"123 Main St\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.data").value("restaurant details already added"));

        verify(restaurantService, times(1)).addRestaurant("Pizza Place", "123 Main St");
    }

    @Test
    void testAddRestaurantWithoutNameAndAddressBadRequest() throws Exception {
        when(restaurantService.addRestaurant(anyString(), anyString()))
                .thenThrow(new CannotCreateRestaurantException("name and address cannot be null or empty"));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"address\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.data").value("name and address cannot be null or empty"));

        verify(restaurantService, times(1)).addRestaurant("", "");
    }

    @Test
    void testGetAllRestaurantSuccess() throws Exception {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("Pizza Place");
        when(restaurant.getAddress()).thenReturn("123 Main St");

        GETResponseDTO responseDTO = new GETResponseDTO();
        responseDTO.setId(1);
        responseDTO.setName("Pizza Place");
        responseDTO.setAddress(Optional.of("123 Main St"));

        when(restaurantService.findAllRestaurants()).thenReturn(List.of(restaurant));
        when(restaurantService.convertToDto(restaurant)).thenReturn(responseDTO);

        mockMvc.perform(get("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Pizza Place"))
                .andExpect(jsonPath("$.data[0].address").value("123 Main St"));

        verify(restaurantService, times(1)).findAllRestaurants();
    }

    @Test
    void testGetAllRestaurantEmptyList() throws Exception {
        when(restaurantService.findAllRestaurants())
                .thenThrow(new RestaurantDoesNotExistException("no restaurants found"));

        mockMvc.perform(get("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("no restaurants found"));

        verify(restaurantService, times(1)).findAllRestaurants();
    }

    @Test
    void testGetRestaurantByIdSuccess() throws Exception {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("Pizza Place");
        when(restaurant.getAddress()).thenReturn("123 Main St");

        GETResponseDTO responseDTO = new GETResponseDTO();
        responseDTO.setId(1);
        responseDTO.setName("Pizza Place");
        responseDTO.setAddress(Optional.of("123 Main St"));
        when(restaurantService.findById(1)).thenReturn(restaurant);
        when(restaurantService.convertToDto(restaurant)).thenReturn(responseDTO);

        mockMvc.perform(get("/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza Place"))
                .andExpect(jsonPath("$.data.address").value("123 Main St"));

        verify(restaurantService, times(1)).findById(1);
    }

    @Test
    void testGetRestaurantByIdNotFound() throws Exception {
        when(restaurantService.findById(1))
                .thenThrow(new RestaurantDoesNotExistException("no restaurants found"));

        mockMvc.perform(get("/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("no restaurants found"));

        verify(restaurantService, times(1)).findById(1);
    }

    @Test
    void testAssignMenuItemsToRestaurantSuccess() throws Exception {
        when(restaurantService.assignMenuItemToRestaurant(1, "1,2,3"))
                .thenReturn("menu items assigned successfully");

        mockMvc.perform(post("/restaurants/{restaurantId}/menu-items", 1)
                        .param("menuItemIds", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").value("menu items assigned successfully"));

        verify(restaurantService, times(1)).assignMenuItemToRestaurant(1, "1,2,3");
    }

    @Test
    void testAssignMenuItemsToRestaurant_RestaurantDoesNotExist() throws Exception {
        when(restaurantService.assignMenuItemToRestaurant(1, "1,2,3"))
                .thenThrow(new RestaurantDoesNotExistException("restaurant does not exist"));

        mockMvc.perform(post("/restaurants/{restaurantId}/menu-items", 1)
                        .param("menuItemIds", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("restaurant does not exist"));

        verify(restaurantService, times(1)).assignMenuItemToRestaurant(1, "1,2,3");
    }

    @Test
    void testAssignMenuItemsToRestaurant_MenuItemDoesNotExist() throws Exception {
        when(restaurantService.assignMenuItemToRestaurant(1, "1,2,3"))
                .thenThrow(new MenuItemDoesNotExistException("one or more menu items do not exist"));

        mockMvc.perform(post("/restaurants/{restaurantId}/menu-items", 1)
                        .param("menuItemIds", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("one or more menu items do not exist"));

        verify(restaurantService, times(1)).assignMenuItemToRestaurant(1, "1,2,3");
    }

    @Test
    void testAssignMenuItemsToRestaurant_MenuItemAlreadyAssigned() throws Exception {
        when(restaurantService.assignMenuItemToRestaurant(1, "1,2,3"))
                .thenThrow(new MenuItemAlreadyAssignedException("menu item already assigned to restaurant"));

        mockMvc.perform(post("/restaurants/{restaurantId}/menu-items", 1)
                        .param("menuItemIds", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.data").value("menu item already assigned to restaurant"));

        verify(restaurantService, times(1)).assignMenuItemToRestaurant(1, "1,2,3");
    }

    @Test
    void testGetMenuItemsByRestaurantIdSuccess() throws Exception {
        MenuItem menuItem1 = mock(MenuItem.class);
        when(menuItem1.getId()).thenReturn(1);
        when(menuItem1.getName()).thenReturn("Pizza");
        when(menuItem1.getPrice()).thenReturn(100);

        MenuItem menuItem2 = mock(MenuItem.class);
        when(menuItem2.getId()).thenReturn(2);
        when(menuItem2.getName()).thenReturn("Burger");
        when(menuItem2.getPrice()).thenReturn(50);
        List<MenuItem> menuItems = List.of(menuItem1, menuItem2);

        when(restaurantService.getMenuItemsByRestaurantId(1)).thenReturn(menuItems);

        mockMvc.perform(get("/restaurants/1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Pizza"))
                .andExpect(jsonPath("$.data[0].price").value(100))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Burger"))
                .andExpect(jsonPath("$.data[1].price").value(50));

        verify(restaurantService, times(1)).getMenuItemsByRestaurantId(1);
    }

    @Test
    void testGetMenuItemsByRestaurantIdRestaurantNotFound() throws Exception {
        when(restaurantService.getMenuItemsByRestaurantId(1))
                .thenThrow(new RestaurantDoesNotExistException("restaurant does not exist"));

        mockMvc.perform(get("/restaurants/1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("restaurant does not exist"));

        verify(restaurantService, times(1)).getMenuItemsByRestaurantId(1);
    }

    @Test
    void testGetSelectedMenuItemByIdSuccess() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getId()).thenReturn(1);
        when(menuItem.getName()).thenReturn("Pizza");
        when(menuItem.getPrice()).thenReturn(100);
        when(restaurantService.getSelectedMenuItemById(1, 1)).thenReturn(menuItem);

        mockMvc.perform(get("/restaurants/1/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza"))
                .andExpect(jsonPath("$.data.price").value(100));

        verify(restaurantService, times(1)).getSelectedMenuItemById(1, 1);
    }

    @Test
    void testGetSelectedMenuItemByIdRestaurantNotFound() throws Exception {
        when(restaurantService.getSelectedMenuItemById(1, 1))
                .thenThrow(new RestaurantDoesNotExistException("restaurant does not exist"));

        mockMvc.perform(get("/restaurants/1/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("restaurant does not exist"));

        verify(restaurantService, times(1)).getSelectedMenuItemById(1, 1);
    }

    @Test
    void testGetSelectedMenuItemByIdMenuItemNotFound() throws Exception {
        when(restaurantService.getSelectedMenuItemById(1, 1))
                .thenThrow(new MenuItemDoesNotExistException("menu item does not exist"));

        mockMvc.perform(get("/restaurants/1/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("menu item does not exist"));

        verify(restaurantService, times(1)).getSelectedMenuItemById(1, 1);
    }
}
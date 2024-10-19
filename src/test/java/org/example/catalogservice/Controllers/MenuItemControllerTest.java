package org.example.catalogservice.Controllers;

import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.*;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Services.MenuItemService;
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

class MenuItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController menuItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(menuItemController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    void tesAddMenuItemSuccess() throws Exception {
        when(menuItemService.addMenuItem(anyString(), anyInt())).thenReturn("menu item added successfully");

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Margherita Pizza\", \"price\": \"80\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data").value("menu item added successfully"));

        verify(menuItemService, times(1)).addMenuItem("Margherita Pizza", 80);
    }

    @Test
    void testAddAlreadyAddedMenuItemBadRequestThrowsException() throws Exception {
        when(menuItemService.addMenuItem(anyString(), anyInt()))
                .thenThrow(new MenuItemAlreadyAddedException("menu item already added"));

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Margherita Pizza\", \"price\": \"80\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.data").value("menu item already added"));

        verify(menuItemService, times(1)).addMenuItem("Margherita Pizza", 80);
    }

    @Test
    void testAddMenuItemWithoutNameAndPriceBadRequest() throws Exception {
        when(menuItemService.addMenuItem(anyString(), anyInt()))
                .thenThrow(new CannotCreateMenuItemException("name cannot be null or empty and price cannot be less than or equal to 0"));

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"price\": \"\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.data")
                        .value("name cannot be null or empty and price cannot be less than or equal to 0"));

        verify(menuItemService, times(1)).addMenuItem("", 0);
    }

    @Test
    void testGetAllMenuItemsSuccess() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getId()).thenReturn(1);
        when(menuItem.getName()).thenReturn("Margherita Pizza");
        when(menuItem.getPrice()).thenReturn(80);

        GETResponseDTO responseDTO = new GETResponseDTO();
        responseDTO.setId(1);
        responseDTO.setName("Margherita Pizza");
        responseDTO.setPrice(Optional.of(80));

        when(menuItemService.findAllMenuItems()).thenReturn(List.of(menuItem));
        when(menuItemService.convertToDto(menuItem)).thenReturn(responseDTO);

        mockMvc.perform(get("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.data[0].price").value(80));

        verify(menuItemService, times(1)).findAllMenuItems();
    }

    @Test
    void testGetAllMenuItemsEmptyList() throws Exception {
        when(menuItemService.findAllMenuItems())
                .thenThrow(new MenuItemDoesNotExistException("no menu items found"));

        mockMvc.perform(get("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data")
                        .value("no menu items found"));

        verify(menuItemService, times(1)).findAllMenuItems();
    }

    @Test
    void testGetMenuItemByIdSuccess() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getId()).thenReturn(1);
        when(menuItem.getName()).thenReturn("Margherita Pizza");
        when(menuItem.getPrice()).thenReturn(80);

        GETResponseDTO responseDTO = new GETResponseDTO();
        responseDTO.setId(1);
        responseDTO.setName("Margherita Pizza");
        responseDTO.setPrice(Optional.of(80));
        when(menuItemService.findById(1)).thenReturn(menuItem);
        when(menuItemService.convertToDto(menuItem)).thenReturn(responseDTO);

        mockMvc.perform(get("/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.data.price").value(80));

        verify(menuItemService, times(1)).findById(1);
    }

    @Test
    void testGetMenuItemByIdNotFound() throws Exception {
        when(menuItemService.findById(1))
                .thenThrow(new MenuItemDoesNotExistException("no menu item found"));

        mockMvc.perform(get("/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.data").value("no menu item found"));

        verify(menuItemService, times(1)).findById(1);
    }
}
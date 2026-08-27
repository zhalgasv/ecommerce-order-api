package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartItemResponse;
import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.security.CurrentUserService;
import com.zhalgas.ecommerceorderapi.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getCurrentUserCart_returnsCartResponse() throws Exception {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setProductId(1L);
        cartItemResponse.setQuantity(1);
        cartItemResponse.setProductName("testProduct");
        cartItemResponse.setUnitPrice(new BigDecimal("10.00"));
        cartItemResponse.setTotalPrice(new BigDecimal("10.00"));

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(1L);
        cartResponse.setUserId(1L);
        cartResponse.setTotalPrice(new BigDecimal("10.00"));
        cartResponse.setItems(List.of(cartItemResponse));

        when(currentUserService.getCurrentUserId()).thenReturn(1L);
        when(cartService.getCartByUserId(1L)).thenReturn(cartResponse);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(10.00))
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].productName").value("testProduct"))
                .andExpect(jsonPath("$.items[0].quantity").value(1));

        verify(currentUserService).getCurrentUserId();
        verify(cartService).getCartByUserId(1L);
    }

    @Test
    void getCurrentUserCart_whenCartDoesNotExist_returnsNotFound() throws Exception {
        when(currentUserService.getCurrentUserId()).thenReturn(1L);
        when(cartService.getCartByUserId(1L)).thenThrow(
                new ResourceNotFoundException("Cart not found for user with id: 1")
        );

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cart not found for user with id: 1"))
                .andExpect(jsonPath("$.path").value("/api/cart"));

        verify(currentUserService).getCurrentUserId();
        verify(cartService).getCartByUserId(1L);
    }

    @Test
    void addProductToCart_whenRequestIsValid_returnsUpdatedCart() throws Exception {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setProductId(10L);
        cartItemResponse.setQuantity(2);

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(1L);
        cartResponse.setItems(List.of(cartItemResponse));

        when(currentUserService.getCurrentUserId()).thenReturn(1L);
        when(cartService.addProductToCart(1L, 10L, 2)).thenReturn(cartResponse);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":10,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(cartService).addProductToCart(1L, 10L, 2);
        verify(currentUserService).getCurrentUserId();
    }
}

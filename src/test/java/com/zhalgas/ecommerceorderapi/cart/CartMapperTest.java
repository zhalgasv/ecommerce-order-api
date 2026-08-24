package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartItemResponse;
import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.cart.mapper.CartMapper;
import com.zhalgas.ecommerceorderapi.product.Product;
import com.zhalgas.ecommerceorderapi.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartMapperTest {

    private final CartMapper cartMapper = new CartMapper();

    @Test
    void toCartResponse_whenCartHasItems_mapsAllFields() {
        User user = new User();
        user.setId(2L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(1L);
        product.setName("testProduct");
        product.setPrice(new BigDecimal("10.00"));
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        cart.addItem(cartItem);

        CartResponse response = cartMapper.toCartResponse(cart);

        CartItemResponse itemResponse = response.getItems().getFirst();

        assertEquals(1L, response.getCartId());
        assertEquals(2L, response.getUserId());
        assertEquals(new BigDecimal("10.00"), response.getTotalPrice());
        assertEquals(1, response.getItems().size());
        assertEquals(1L, itemResponse.getProductId());
        assertEquals(1, itemResponse.getQuantity());

        assertEquals("testProduct", itemResponse.getProductName());
        assertEquals(new BigDecimal("10.00"), itemResponse.getUnitPrice());
        assertEquals(new BigDecimal("10.00"), itemResponse.getTotalPrice());
    }
}

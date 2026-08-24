package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.cart.mapper.CartMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    CartMapper cartMapper;

    @InjectMocks
    CartService cartService;

    @Test
    void getCartByUserId_whenCartExists_returnsCartResponse() {
        Cart cart = new Cart();
        cart.setId(1L);

        CartResponse response = new CartResponse();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartMapper.toCartResponse(cart)).thenReturn(response);

        CartResponse result = cartService.getCartByUserId(1L);

        assertSame(response, result);

        verify(cartRepository).findByUserId(1L);
        verify(cartMapper).toCartResponse(cart);
    }

    @Test
    void getCartByUserId_whenCartDoesNotExist_throwsResourceNotFoundException() {
    }
}

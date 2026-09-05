package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.cart.mapper.CartMapper;
import com.zhalgas.ecommerceorderapi.exception.BadRequestException;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.product.Product;
import com.zhalgas.ecommerceorderapi.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

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
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getCartByUserId(1L)
        );

        assertEquals(
                "Cart not found for user with id: 1",
                exception.getMessage()
        );

        verify(cartRepository).findByUserId(1L);
        verifyNoInteractions(cartMapper);
    }

    @Test
    void addProductToCart_whenProductIsNew_addsCartItem() {
        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(5);

        CartResponse response = new CartResponse();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(cartMapper.toCartResponse(cart)).thenReturn(response);

        CartResponse result = cartService.addProductToCart(1L, 10L, 5);
        assertSame(response, result);

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(captor.capture());

        CartItem capturedCartItem = captor.getValue();
        assertSame(cart, capturedCartItem.getCart());
        assertSame(product, capturedCartItem.getProduct());
        assertEquals(5, capturedCartItem.getQuantity());
    }

    @Test
    void addProductToCart_whenProductAlreadyExists_increasesQuantity() {
        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setQuantity(2);
        cartItem.setProduct(product);
        cart.addItem(cartItem);

        CartResponse response = new CartResponse();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.of(cartItem));
        when(cartMapper.toCartResponse(cart)).thenReturn(response);

        CartResponse result = cartService.addProductToCart(1L, 10L, 3);

        assertSame(response, result);
        assertEquals(5, cartItem.getQuantity());

        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void addProductToCart_whenStockIsNotEnough_throwsBadRequestException() {
        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(5);

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(4);
        cartItem.setProduct(product);
        cart.addItem(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.of(cartItem));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cartService.addProductToCart(1L, 10L, 2)
        );

        assertEquals(
                "Not enough stock for product with id: 10",
                exception.getMessage()
        );
        assertEquals(4, cartItem.getQuantity());

        verify(cartRepository).findByUserId(1L);
        verifyNoInteractions(cartMapper);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addProductToCart_whenProductDoesNotExist_throwsResourceNotFoundException() {
        Cart cart = new Cart();
        cart.setId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> cartService.addProductToCart(1L, 10L, 2));

        assertEquals(
                "Product not found with id: 10",
                exception.getMessage()
        );

        verify(cartRepository).findByUserId(1L);
        verify(productRepository).findById(10L);
        verifyNoInteractions(cartMapper);
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void removeProductFromCart_whenItemExists_removesItem() {
        Cart cart = new Cart();
        cart.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setId(2L);

        CartResponse cartResponse = new CartResponse();

        cart.addItem(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.of(cartItem));
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.removeProductFromCart(1L, 10L);
        assertEquals(0, cart.getItems().size());
        assertSame(cartResponse, result);
        assertNull(cartItem.getCart());
    }

    @Test
    void removeProductFromCart_whenItemDoesNotExist_throwsResourceNotFoundException() {
        Cart cart = new Cart();
        cart.setId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.removeProductFromCart(1L, 10L)
        );

        assertEquals(
                "Product with id: 10 not found in cart",
                exception.getMessage()
        );

        verify(cartRepository).findByUserId(1L);
        verify(cartItemRepository).findByCartIdAndProductId(1L, 10L);
        verifyNoInteractions(cartMapper);
    }
}

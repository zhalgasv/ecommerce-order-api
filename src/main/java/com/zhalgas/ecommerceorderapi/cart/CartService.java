package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.cart.mapper.CartMapper;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional(readOnly = true)
    public Cart findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with id: " + userId));
    }

    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = findByUserId(userId);
        return cartMapper.toCartResponse(cart);
    }
}

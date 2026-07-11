package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  private final CartRepository cartRepository;

  public CartService(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  @Transactional(readOnly = true)
  public Cart findByUserId(Long userId) {
      return cartRepository.findByUserId(userId)
              .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with id: " + userId));
  }
}

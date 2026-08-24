package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    private final CurrentUserService currentUserService;

    public CartController(CartService cartService, CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public CartResponse getCurrentUserCart() {
        Long userId = currentUserService.getCurrentUserId();
        return cartService.getCartByUserId(userId);
    }
}

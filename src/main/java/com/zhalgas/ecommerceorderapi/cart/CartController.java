package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartItemRequest;
import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/items")
    public CartResponse createCartItem(@Valid @RequestBody CartItemRequest cartItemRequest) {
        Long userId = currentUserService.getCurrentUserId();

        return cartService.addProductToCart(userId, cartItemRequest.getProductId(), cartItemRequest.getQuantity());
    }
}

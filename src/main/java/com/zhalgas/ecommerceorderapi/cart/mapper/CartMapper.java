package com.zhalgas.ecommerceorderapi.cart.mapper;

import com.zhalgas.ecommerceorderapi.cart.Cart;
import com.zhalgas.ecommerceorderapi.cart.CartItem;
import com.zhalgas.ecommerceorderapi.cart.dto.CartItemResponse;
import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice(),
                cartItem.getTotalPrice()
        );
    }

    public List<CartItemResponse> toCartItemResponseList(List<CartItem> items) {
        return items.stream()
                .map(this::toCartItemResponse)
                .toList();
    }

    public CartResponse toCartResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                cart.getTotalPrice(),
                toCartItemResponseList(cart.getItems())
        );
    }
}

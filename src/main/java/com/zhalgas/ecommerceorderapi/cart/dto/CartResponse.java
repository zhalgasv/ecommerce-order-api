package com.zhalgas.ecommerceorderapi.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartResponse {

    private Long cartId;

    private Long userId;

    private BigDecimal totalPrice;

    private List<CartItemResponse> items;

    public CartResponse(Long cartId, Long userId, BigDecimal totalPrice, List<CartItemResponse> items) {
        this.cartId = cartId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.items = items;
    }
}

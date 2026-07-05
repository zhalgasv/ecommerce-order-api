package com.zhalgas.ecommerceorderapi.order.dto;

import com.zhalgas.ecommerceorderapi.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;

    private OrderStatus status;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId, OrderStatus status, BigDecimal totalPrice, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
    }
}

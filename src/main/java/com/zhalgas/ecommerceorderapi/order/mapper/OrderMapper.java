package com.zhalgas.ecommerceorderapi.order.mapper;

import com.zhalgas.ecommerceorderapi.order.Order;
import com.zhalgas.ecommerceorderapi.order.OrderItem;
import com.zhalgas.ecommerceorderapi.order.dto.OrderItemResponse;
import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getTotalPrice()
        );
    }


    public List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                toOrderItemResponseList(order.getItems())
        );
    }
}

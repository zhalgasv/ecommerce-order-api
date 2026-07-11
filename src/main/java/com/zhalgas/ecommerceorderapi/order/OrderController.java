package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping("/checkout/{userId}")
    public OrderResponse createOrderFromCart(
        @PathVariable
        Long userId) {
        return orderService.createOrderFromCart(userId);
    }
}

package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import com.zhalgas.ecommerceorderapi.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public OrderController(OrderService orderService, CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/my")
    public List<OrderResponse> getMyOrders() {
        Long userId = currentUserId();
        return orderService.getOrdersByUserId(userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        Long userId = currentUserId();
        return orderService.getOrderByIdForUser(orderId, userId);
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long orderId) {
        Long userId = currentUserId();
        return orderService.cancelOrderForUser(orderId, userId);
    }

    @PatchMapping("/{orderId}/complete")
    public OrderResponse completeOrder(@PathVariable Long orderId) {
        Long userId = currentUserId();
        return orderService.completeOrderForUser(orderId, userId);
    }

    @PostMapping("/checkout")
    public OrderResponse createOrderFromCurrentUser() {
        Long userId = currentUserId();
        return orderService.createOrderFromCart(userId);
    }

    private Long currentUserId() {
        return currentUserService.getCurrentUserId();
    }
}

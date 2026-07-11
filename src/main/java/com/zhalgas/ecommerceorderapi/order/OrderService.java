package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.cart.Cart;
import com.zhalgas.ecommerceorderapi.cart.CartService;
import com.zhalgas.ecommerceorderapi.exception.BadRequestException;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import com.zhalgas.ecommerceorderapi.order.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartService cartService;


    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.cartService = cartService;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id: " + orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse createOrderFromCart(Long userId) {
        Cart cart = cartService.findByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty for user with id: " + userId);
        }
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        cart.getItems().forEach(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProduct(item.getProduct());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            order.addItem(orderItem);
        });
        Order savedOrder = orderRepository.save(order);
        cart.clearItems();
        return orderMapper.toOrderResponse(savedOrder);
    }
}


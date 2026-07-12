package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.cart.Cart;
import com.zhalgas.ecommerceorderapi.cart.CartService;
import com.zhalgas.ecommerceorderapi.exception.BadRequestException;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import com.zhalgas.ecommerceorderapi.order.mapper.OrderMapper;
import com.zhalgas.ecommerceorderapi.product.Product;
import com.zhalgas.ecommerceorderapi.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, CartService cartService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.cartService = cartService;
        this.productRepository = productRepository;
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
        validateCartIsNotEmpty(cart, userId);
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        cart.getItems().forEach(item -> {
            Product product = item.getProduct();
            validateStock(product, item.getQuantity());
            decreaseStock(product, item.getQuantity());
            productRepository.save(product);
            OrderItem orderItem = createOrderItem(product, item.getQuantity());
            order.addItem(orderItem);
        });
        Order savedOrder = orderRepository.save(order);
        cart.clearItems();
        return orderMapper.toOrderResponse(savedOrder);
    }

    private void validateCartIsNotEmpty(Cart cart, Long userId) {
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty for user with id: " + userId);
        }
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Not enough stock for product: " + product.getName() + " with id: " + product.getId());
        }
    }

    private void decreaseStock(Product product, int quantity) {
        Integer currentStock = product.getStockQuantity();
        product.setStockQuantity(currentStock - quantity);
    }

    private OrderItem createOrderItem(Product product, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setProduct(product);
        orderItem.setUnitPrice(product.getPrice());
        return orderItem;
    }
}


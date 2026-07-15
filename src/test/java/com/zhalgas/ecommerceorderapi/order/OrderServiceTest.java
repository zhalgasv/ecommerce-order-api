package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.cart.Cart;
import com.zhalgas.ecommerceorderapi.cart.CartItem;
import com.zhalgas.ecommerceorderapi.cart.CartService;
import com.zhalgas.ecommerceorderapi.user.User;
import com.zhalgas.ecommerceorderapi.exception.BadRequestException;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import com.zhalgas.ecommerceorderapi.order.mapper.OrderMapper;
import com.zhalgas.ecommerceorderapi.product.Product;
import com.zhalgas.ecommerceorderapi.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartService cartService;

    @Test
    void getOrderById_whenOrderExists_returnsOrderResponse() {
        Order order = new Order();
        order.setOrderId(1L);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.getOrderById(1L);

        assertEquals(orderResponse, result);
        verify(orderRepository).findById(1L);
        verify(orderMapper).toOrderResponse(order);
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_throwsResourceNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderMapper, never()).toOrderResponse(any());
    }

    @Test
    void completeOrder_whenOrderIsPending_returnsCompletedOrderResponse() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setOrderId(1L);

        OrderResponse response = new OrderResponse();
        response.setOrderId(1L);
        response.setStatus(OrderStatus.COMPLETED);

        when(orderMapper.toOrderResponse(order)).thenReturn(response);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponse result = orderService.completeOrder(1L);

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(response, result);
        verify(orderRepository).findById(1L);
        verify(orderMapper).toOrderResponse(order);
        verify(orderRepository).save(order);
    }

    @Test
    void completeOrder_whenOrderIsCancelled_throwsBadRequestException() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.completeOrder(1L));

        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toOrderResponse(any());
    }

    @Test
    void completeOrder_whenOrderIsAlreadyCompleted_throwsBadRequestException() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.completeOrder(1L));

        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toOrderResponse(any());
    }

    @Test
    void cancelOrder_whenOrderIsPending_returnsCancelledOrderResponse() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderResponse response = new OrderResponse();
        response.setOrderId(1L);
        response.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponse(order)).thenReturn(response);

        OrderResponse result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(response, result);

        verify(orderRepository).findById(1L);
        verify(orderMapper).toOrderResponse(order);
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_whenOrderIsCompleted_throwsBadRequestException() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toOrderResponse(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void cancelOrder_whenOrderIsAlreadyCancelled_throwsBadRequestException() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toOrderResponse(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void cancelOrder_whenOrderHasItems_restoresProductStock() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.PENDING);

        Product product = new Product();
        product.setStockQuantity(5);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        order.addItem(orderItem);

        OrderResponse orderResponse = new OrderResponse();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.cancelOrder(1L);

        assertEquals(7, product.getStockQuantity());
        assertEquals(orderResponse, result);

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
        verify(orderMapper).toOrderResponse(order);
    }

    @Test
    void createOrderFromCart_whenCartIsEmpty_throwsBadRequestException() {
        Cart cart = new Cart();
        when(cartService.findByUserId(1L)).thenReturn(cart);

        assertThrows(BadRequestException.class, () -> orderService.createOrderFromCart(1L));

        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createOrderFromCart_whenProductStockIsNotEnough_throwsBadRequestException() {
        User user = new User();

        Cart cart = new Cart();
        cart.setUser(user);

        Product product = new Product();
        product.setStockQuantity(1);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart.addItem(cartItem);

        when(cartService.findByUserId(1L)).thenReturn(cart);

        assertThrows(BadRequestException.class, () -> orderService.createOrderFromCart(1L));

        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
        verify(cartService).findByUserId(1L);
    }

    @Test
    void createOrderFromCart_whenCartHasItems_createsOrderAndDecreasesStock() {
        User user = new User();

        Cart cart = new Cart();
        cart.setUser(user);

        Product product = new Product();
        product.setStockQuantity(5);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);

        OrderResponse response = new OrderResponse();

        when(cartService.findByUserId(1L)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(response);

        OrderResponse result = orderService.createOrderFromCart(1L);

        assertEquals(3, product.getStockQuantity());
        assertEquals(response, result);
        assertTrue(cart.getItems().isEmpty());

        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(product);
        verify(cartService).findByUserId(1L);
        verify(orderMapper).toOrderResponse(any(Order.class));
    }
}

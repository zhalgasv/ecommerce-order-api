package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.cart.CartService;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import com.zhalgas.ecommerceorderapi.order.mapper.OrderMapper;
import com.zhalgas.ecommerceorderapi.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
}

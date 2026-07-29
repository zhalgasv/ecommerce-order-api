package com.zhalgas.ecommerceorderapi.order;

import com.zhalgas.ecommerceorderapi.order.dto.OrderResponse;
import com.zhalgas.ecommerceorderapi.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CurrentUserService currentUserService;

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 10L;

    @Test
    void getMyOrders_returnsCurrentUserOrders() throws Exception {
        OrderResponse response = createOrderResponse();
        List<OrderResponse> responses = List.of(response);

        when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
        when(orderService.getOrdersByUserId(USER_ID)).thenReturn(responses);

        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(ORDER_ID))
                .andExpect(jsonPath("$[0].userId").value(USER_ID));

        verify(currentUserService).getCurrentUserId();
        verify(orderService).getOrdersByUserId(USER_ID);
    }

    @Test
    void getOrderById_returnsCurrentUserOrder() throws Exception {
        OrderResponse response = createOrderResponse();

        when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
        when(orderService.getOrderByIdForUser(ORDER_ID, USER_ID)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{orderId}", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(currentUserService).getCurrentUserId();
        verify(orderService).getOrderByIdForUser(ORDER_ID, USER_ID);
    }

    @Test
    void checkout_createsOrderFromCurrentUserCart() throws Exception {
        OrderResponse response = createOrderResponse();

        when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
        when(orderService.createOrderFromCart(USER_ID)).thenReturn(response);

        mockMvc.perform(post("/api/orders/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(currentUserService).getCurrentUserId();
        verify(orderService).createOrderFromCart(USER_ID);
    }

    @Test
    void cancelOrder_cancelsCurrentUserOrder() throws Exception {
        OrderResponse response = createOrderResponse();

        when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
        when(orderService.cancelOrderForUser(ORDER_ID, USER_ID)).thenReturn(response);

        mockMvc.perform(patch("/api/orders/{orderId}/cancel", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(currentUserService).getCurrentUserId();
        verify(orderService).cancelOrderForUser(ORDER_ID, USER_ID);
    }

    @Test
    void completeOrder_completesCurrentUserOrder() throws Exception {
        OrderResponse response = createOrderResponse();

        when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
        when(orderService.completeOrderForUser(ORDER_ID, USER_ID)).thenReturn(response);

        mockMvc.perform(patch("/api/orders/{orderId}/complete", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(currentUserService).getCurrentUserId();
        verify(orderService).completeOrderForUser(ORDER_ID, USER_ID);
    }

    private OrderResponse createOrderResponse() {
        OrderResponse response = new OrderResponse();
        response.setOrderId(ORDER_ID);
        response.setUserId(USER_ID);
        return response;
    }
}

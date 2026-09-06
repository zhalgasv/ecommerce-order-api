package com.zhalgas.ecommerceorderapi.product;

import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.product.dto.ProductResponse;
import com.zhalgas.ecommerceorderapi.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getProductById_whenProductExists_returnsProductResponse() throws Exception {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setStockQuantity(10);

        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        verify(productService).getProductById(1L);
    }

    @Test
    void getAllProducts_returnsProductResponses() throws Exception {
        ProductResponse productResponse1 = new ProductResponse();
        productResponse1.setId(1L);
        productResponse1.setName("Test Product 1");
        productResponse1.setStockQuantity(10);

        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId(2L);
        productResponse2.setName("Test Product 2");
        productResponse2.setStockQuantity(5);

        when(productService.getAllProducts()).thenReturn(List.of(productResponse1, productResponse2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Product 1"))
                .andExpect(jsonPath("$[0].stockQuantity").value(10))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test Product 2"))
                .andExpect(jsonPath("$[1].stockQuantity").value(5));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_whenProductDoesNotExist_returnsNotFound() throws Exception {
        when(productService.getProductById(1L)).thenThrow(
                new ResourceNotFoundException("Product not found with id: 1")
        );

        mockMvc.perform(get("/api/products/{productId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: 1"))
                .andExpect(jsonPath("$.path").value("/api/products/1"));

        verify(productService).getProductById(1L);
    }
}

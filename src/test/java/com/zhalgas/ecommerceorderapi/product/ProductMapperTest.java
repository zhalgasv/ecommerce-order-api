package com.zhalgas.ecommerceorderapi.product;

import com.zhalgas.ecommerceorderapi.category.Category;
import com.zhalgas.ecommerceorderapi.product.dto.ProductResponse;
import com.zhalgas.ecommerceorderapi.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    @Test
    void toProductResponse_whenProductIsValid_mapsAllFields() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        product.setCategory(category);

        ProductResponse result = productMapper.toProductResponse(product);

        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals(10, result.getStockQuantity());
        assertEquals(1L, result.getCategoryId());
        assertEquals("Test Category", result.getCategoryName());
    }
}

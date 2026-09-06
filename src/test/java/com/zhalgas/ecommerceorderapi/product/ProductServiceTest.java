package com.zhalgas.ecommerceorderapi.product;

import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.product.dto.ProductResponse;
import com.zhalgas.ecommerceorderapi.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductById_whenProductExists_returnsProductResponse() {
        Product product = new Product();
        product.setId(1L);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toProductResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getProductById(1L);

        assertSame(productResponse, result);

        verify(productRepository).findById(1L);
        verify(productMapper).toProductResponse(product);
    }

    @Test
    void getProductById_whenProductDoesNotExist_throwsResourceNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(1L)
        );

        assertEquals("Product not found with id: 1", exception.getMessage());

        verify(productRepository).findById(1L);
        verifyNoInteractions(productMapper);
    }

    @Test
    void getAllProducts_returnsProductResponses() {
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        ProductResponse productResponse1 = new ProductResponse();
        productResponse1.setId(1L);

        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId(2L);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toProductResponseList(List.of(product1, product2)))
                .thenReturn(List.of(productResponse1, productResponse2));

        List<ProductResponse> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertSame(productResponse1, result.get(0));
        assertSame(productResponse2, result.get(1));

        verify(productRepository).findAll();
        verify(productMapper).toProductResponseList(List.of(product1, product2));
    }
}

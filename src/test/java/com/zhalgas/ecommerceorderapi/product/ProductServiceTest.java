package com.zhalgas.ecommerceorderapi.product;

import com.zhalgas.ecommerceorderapi.common.dto.PageResponse;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.product.dto.ProductResponse;
import com.zhalgas.ecommerceorderapi.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void getAllProducts_returnsPagedProductResponses() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        ProductResponse productResponse1 = new ProductResponse();
        productResponse1.setId(1L);

        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId(2L);

        Page<Product> productPage = new PageImpl<>(
                List.of(product1, product2),
                pageable,
                5
        );

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toProductResponseList(List.of(product1, product2)))
                .thenReturn(List.of(productResponse1, productResponse2));

        PageResponse<ProductResponse> result = productService.getAllProducts(pageable);

        assertEquals(2, result.getContent().size());
        assertSame(productResponse1, result.getContent().get(0));
        assertSame(productResponse2, result.getContent().get(1));
        assertEquals(0, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertFalse(result.isLast());

        verify(productRepository).findAll(pageable);
        verify(productMapper).toProductResponseList(List.of(product1, product2));
    }
}

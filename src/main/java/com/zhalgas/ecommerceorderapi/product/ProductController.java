package com.zhalgas.ecommerceorderapi.product;

import com.zhalgas.ecommerceorderapi.common.dto.PageResponse;
import com.zhalgas.ecommerceorderapi.product.dto.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ProductResponse getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    @GetMapping
    public PageResponse<ProductResponse> getAllProducts(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return productService.getAllProducts(pageable);
    }
}

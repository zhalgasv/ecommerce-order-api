package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cart_id",
            foreignKey = @ForeignKey(name = "fk_cart_items_cart"),
            nullable = false
    )
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            foreignKey = @ForeignKey(name = "fk_cart_items_product"),
            nullable = false
    )
    private Product product;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;
}

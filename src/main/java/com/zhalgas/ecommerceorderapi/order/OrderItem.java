package com.zhalgas.ecommerceorderapi.order;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderItem;

    @Column(nullable = false)
    private int totalItems;

    @Column(name = "total_price", nullable = false)
    private BigDecimal price;
}

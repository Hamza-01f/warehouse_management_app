package com.brief.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SalesOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    private BigDecimal price;

    @Column(name = "quantity_reserved")
    @Builder.Default
    private Integer quantityReserved;

    @Column(name = "quantity_fulfilled")
    @Builder.Default
    private Integer quantityFulfilled = 0;

    @Column(name = "backorder_quantity")
    @Builder.Default
    private Integer backorderQuantity = 0;
}

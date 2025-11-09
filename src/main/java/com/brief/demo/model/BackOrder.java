// Backorder.java
package com.brief.demo.model;

import com.brief.demo.enums.BackOrderStatus;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backorders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_line_id")
    private SalesOrderLine salesOrderLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime fulfilledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackOrderStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = BackOrderStatus.PENDING;
        }
    }
}

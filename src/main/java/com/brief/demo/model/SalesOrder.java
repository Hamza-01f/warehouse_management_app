package com.brief.demo.model;

import com.brief.demo.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String country;

    private String city;

    private String street;

    private String zipCode;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesOrderLine> orderLines = new ArrayList<>();

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private Shipment shipment;

    @Column
    private LocalDateTime reservedAt;

    @Column
    private LocalDateTime shippedAt;

    @Column
    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.CREATED;
        }
    }

    public boolean canBeReserved() {
        return status == OrderStatus.CREATED;
    }

    public boolean canBeShipped() {
        return status == OrderStatus.RESERVED;
    }

    public boolean canBeDelivered() {
        return status == OrderStatus.SHIPPED;
    }
}

package com.brief.demo.model;

import com.brief.demo.enums.POStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private POStatus status;

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    private LocalDate approvedAt;
    private LocalDate receivedAt;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<POLine> orderLines = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backorder_id")
    private BackOrder backOrder;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        if (status == null) {
            status = POStatus.CREATED;
        }
    }
}

package com.brief.demo.model;

import com.brief.demo.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }

}

package com.brief.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity_on_hand", nullable = false)
    @Builder.Default
    private Integer quantityOnHand = 0;

    @Column(name = "quantity_reserved", nullable = false)
    @Builder.Default
    private Integer quantityReserved = 0;


//    @Column(name = "available_quanitity")
//    private Integer available_quantity;

//    @PrePersist
//    @PreUpdate
//    public void getAvailableQuantity() {
//        this.available_quantity = quantityOnHand - quantityReserved;
//    }
@Transient
public Integer getAvailable_Quantity() {
    return quantityOnHand - quantityReserved;
}

    public void reserveQuantity(Integer quantity) {
        if (quantity > getAvailable_Quantity()) {
            throw new IllegalStateException("Insufficient available quantity");
        }
        this.quantityReserved += quantity;
    }

    public void releaseReservedQuantity(Integer quantity) {
        if (quantity > this.quantityReserved) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }
        this.quantityReserved -= quantity;
    }

    public void adjustQuantityOnHand(Integer newQuantity) {
        if (newQuantity < this.quantityReserved) {
            throw new IllegalStateException("Quantity on hand cannot be less than reserved quantity");
        }
        this.quantityOnHand = newQuantity;
    }
}

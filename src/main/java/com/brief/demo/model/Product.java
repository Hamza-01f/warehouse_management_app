package com.brief.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String image;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String unit;

    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    public void generatSku(){
        if(this.sku == null){
            this.sku = "PROD-" + UUID.randomUUID().toString().substring(0 , 8).toUpperCase();
        }
    }
}
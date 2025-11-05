package com.brief.demo.model;


import jakarta.persistence.*;
import lombok.*;

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

}

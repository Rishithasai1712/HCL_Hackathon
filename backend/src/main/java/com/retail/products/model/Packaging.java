package com.retail.products.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "packaging")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Packaging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String type; // Box, Bottle, Packet

    @Column(columnDefinition = "TEXT")
    private String description;
}

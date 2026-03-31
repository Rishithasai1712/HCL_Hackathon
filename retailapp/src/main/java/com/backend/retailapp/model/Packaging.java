package com.backend.retailapp.model;

import com.backend.retailapp.model.enums.PackagingType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packaging")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Packaging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackagingType type;

    @Column
    private String description;

    // One packaging → many products
    @OneToMany(mappedBy = "packaging", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
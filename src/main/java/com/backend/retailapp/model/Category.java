package com.backend.retailapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    // Self-referencing parent (e.g. Food → Pizza)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // Child categories
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    // One category → many products
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
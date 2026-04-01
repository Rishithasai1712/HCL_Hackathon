package com.retail.ordering.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "category")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Category> children;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Product> products;
}

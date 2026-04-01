package com.retail.ordering.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "packaging")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Packaging {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type;

    private String description;

    @OneToMany(mappedBy = "packaging")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Product> products;
}

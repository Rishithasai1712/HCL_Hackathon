package com.retail.ordering.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "brand")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Brand {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "is_active")
    private boolean isActive = true;

    @OneToMany(mappedBy = "brand")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Product> products;
}

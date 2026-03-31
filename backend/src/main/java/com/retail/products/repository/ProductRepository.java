package com.retail.products.repository;

import com.retail.products.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Fetch products with optional brand, category, and keyword filters.
     * Only active products are returned by default unless admin view.
     */
    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand b
        JOIN FETCH p.category c
        JOIN FETCH p.packaging pk
        WHERE (:brandId IS NULL OR b.id = :brandId)
          AND (:categoryId IS NULL OR c.id = :categoryId)
          AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND p.active = true
        """)
    Page<Product> findWithFilters(
            @Param("brandId") Long brandId,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /** Admin view — includes inactive products */
    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand b
        JOIN FETCH p.category c
        JOIN FETCH p.packaging pk
        WHERE (:brandId IS NULL OR b.id = :brandId)
          AND (:categoryId IS NULL OR c.id = :categoryId)
          AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Product> findAllWithFilters(
            @Param("brandId") Long brandId,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p JOIN FETCH p.brand JOIN FETCH p.category JOIN FETCH p.packaging WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);
}

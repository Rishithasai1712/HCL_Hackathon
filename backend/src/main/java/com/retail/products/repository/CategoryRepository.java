package com.retail.products.repository;

import com.retail.products.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Returns only root-level categories (no parent) */
    List<Category> findByParentIsNull();

    /** Returns children of a specific parent */
    List<Category> findByParentId(Long parentId);

    /** Fetch full hierarchy eagerly */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findRootCategoriesWithChildren();
}

package com.retail.ordering.repository;
import com.retail.ordering.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderedAtDesc(Long userId);
}

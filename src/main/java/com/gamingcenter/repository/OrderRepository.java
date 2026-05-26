package com.gamingcenter.repository;

import com.gamingcenter.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByShiftId(Long shiftId);

    @EntityGraph(attributePaths = {"items", "items.product"})
    @Override
    List<Order> findAll();
}

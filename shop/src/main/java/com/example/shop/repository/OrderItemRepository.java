package com.example.shop.repository;

import com.example.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

//주문 엔티티 조회

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

package com.example.shop.repository;

import com.example.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

//장바구니 엔티티 조회 테스트를 위해 기재

public interface CartRepository extends JpaRepository<Cart, Long> {
}

package com.ecovery.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 장바구니 상품 조회 Mapper Test
 * @author : 방희경
 * @fileName : CartItemMapperTest
 * @since : 250725
 */

@SpringBootTest
@Slf4j
class CartItemMapperTest {

    @Autowired
    private CartItemMapper cartItemMapper;

}
package com.ecovery.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 상품 Service Test
 * @author : sehui
 * @fileName : ItemServiceTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 Test 추가
 */

@SpringBootTest
@Slf4j
class ItemServiceTest {

    @Autowired
    private ItemService service;

    @Test
    @DisplayName("상품 단 건 조회")
    public void testGetItem(){
        log.info("Item : {}", service.getItemDtl(1L));
    }

}
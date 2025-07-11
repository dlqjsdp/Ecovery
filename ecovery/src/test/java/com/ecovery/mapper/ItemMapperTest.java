package com.ecovery.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * 에코마켓 상품 Mapper Test
 * @author : sehui
 * @fileName : ItemMapperTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 기능 Test 추가
 */

@SpringBootTest
@Slf4j
class ItemMapperTest {

    @Autowired
    private ItemMapper mapper;

    @Test
    @DisplayName("상품 단 건 조회")
    public void testGetItem() {
        log.info("Item : {}", mapper.getItemDtl(1L));
    }
}
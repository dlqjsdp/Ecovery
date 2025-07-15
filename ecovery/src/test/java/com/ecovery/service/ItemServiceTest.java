package com.ecovery.service;

import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * 에코마켓 상품 Service Test
 * @author : sehui
 * @fileName : ItemServiceTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 Test 추가
 *  - 250715 | sehui | 전체 상품 조회 Test 추가
 */

@SpringBootTest
@Slf4j
class ItemServiceTest {

    @Autowired
    private ItemService service;

    @Test
    @DisplayName("전체 상품 조회")
    public void testItemList(){

        //given : 페이징 처리 조건 생성
        String itemNm = "";
        String category = "";
        Criteria cri = new Criteria();

        //when : 전체 상품 조회
        List<ItemListDto> itemList = service.getItemList(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(itemList, "상품 목록이 null입니다.");
        assertFalse(itemList.isEmpty(), "상품 목록이 비어있습니다.");

        for(ItemListDto item : itemList) {
            log.info("Item >> : {} ", item);
        }
    }

    @Test
    @DisplayName("상품 단 건 조회")
    public void testGetItem(){

        //given : 조회할 상품 ID
        Long itemId = 3L;

        //when : itemId로 상품 단 건 조회
        ItemFormDto item = service.getItemDtl(itemId);

        //then : 결과 검증
        assertNotNull(item, "조회된 상품이 null입니다.");
        assertEquals(itemId, item.getItemId(), "상품 ID가 일치하지 않습니다.");

        log.info("Item : {}", item);
    }
}
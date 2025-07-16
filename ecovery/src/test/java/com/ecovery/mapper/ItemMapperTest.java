package com.ecovery.mapper;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemListDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

/*
 * 에코마켓 상품 Mapper Test
 * @author : sehui
 * @fileName : ItemMapperTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 Test 추가
 *  - 250714 | sehui | 전체 상품 조회 Test 추가
 *  - 250715 | sehui | 전체 상품의 수 조회 Test 추가
 *  - 250715 | sehui | 상품 단일 조건 검색 Test 추가
 *  - 250716 | sehui | 상품 등록 Test 추가
 */

@SpringBootTest
@Slf4j
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    @DisplayName("상품 전체 조회 - 조건 없이")
    public void testGetList(){

        //given : 페이징 처리 조건 생성
        String itemNm = "";
        String category = "";
        Criteria cri = new Criteria(1, 10);

        //when : 전체 상품 조회
        List<ItemListDto> itemList = itemMapper.getListWithPage(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(itemList, "조회된 상품 목록이 null입니다.");
        assertFalse(itemList.isEmpty(), "조회된 상품 목록이 비어있습니다.");

        for(ItemListDto item : itemList) {
            log.info("Item >> : {} ", item);
        }
    }

    @Test
    @DisplayName("전체 상품의 수 조회 - 조건 없이")
    public void testGetTotalCount(){

        //given : 페이징 처리 조건 생성
        String itemNm = "";
        String category = "";

        //when : 전체 상품의 수 조회
        int totalCount = itemMapper.getTotalCount(itemNm, category);

        //then : 결과 검증
        assertNotNull(totalCount, "조회된 상품의 수가 null입니다.");

        log.info("Total Count >> : {}", totalCount);
    }

    @Test
    @DisplayName("상품 단 건 조회")
    public void testGetItem() {

        //given : 조회할 상품 ID
        Long itemId = 4L;

        //when : 상품 단 건 조회
        ItemVO item = itemMapper.getItemDtl(itemId);

        //then : 결과 검증
        assertNotNull(item, "해당 ID의 상품이 존재하지 않습니다.");

        log.info("Item : {}", item);
    }

    @Test
    @DisplayName("상품 단일 조건 검색 - 상품명")
    public void testSearch(){

        //given : 조건 설정
        String itemNm = "test";
        String category = "";
        Criteria cri = new Criteria(1, 10);

        //when : 상품 조건 검색 조회
        List<ItemListDto> list = itemMapper.getListWithPage(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(list, "조건을 만족하는 상품이 존재하지 않습니다.");

        list.forEach(item -> System.out.println("list >> " + item));
    }

    @Test
    @DisplayName("상품 등록")
    public void testInsert(){

        //given : 상품 정보 생성
        ItemVO item = ItemVO.builder()
                .itemName("test")
                .price(3000)
                .stockNumber(5)
                .category("가전")
                .itemDetail("test 가전제품")
                .itemSellStatus(ItemSellStatus.SELL)
                .build();

        //when : 상품 등록
        itemMapper.insertItem(item);

        //then : itemId 자동 생성되므로 값 설정 확인
        assertNotNull(item.getItemId(),"상품 ID가 생성되지 않았습니다.");
    }
}
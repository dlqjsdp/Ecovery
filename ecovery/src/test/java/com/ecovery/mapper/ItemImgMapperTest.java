package com.ecovery.mapper;

import com.ecovery.domain.ItemImgVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 상품 이미지 Mapper Test
 * @author : sehui
 * @fileName : ItemImgMapperTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 이미지 전체 조회 Test 추가
 *  - 250716 | sehui | 상품 이미지 수정 Test 추가
 *  - 250716 | sehui | 상품 단건 조회 Test 추가
 *  - 250718 | sehui | 상품 이미지 삭제 Test 추가
 */

@SpringBootTest
@Slf4j
class ItemImgMapperTest {

    @Autowired
    private ItemImgMapper itemImgMapper;

    @Test
    @DisplayName("상품 이미지 전체 조회")
    public void testGetList(){

        //given : 조회할 상품 ID
        Long itemId = 3L;

        //when : 상품 이미지 전체 조회
        List<ItemImgVO> list = itemImgMapper.getItemList(itemId);

        //then : 결과 검증
        assertNotNull(list, "해당 ID 상품에 이미지가 존재하지 않습니다.");

        for(ItemImgVO vo : list){
            log.info("list : {}", vo);
        }
    }

    @Test
    @DisplayName("상품 이미지 등록")
    public void testInsertImg(){

        //gien : 등록할 상품 이미지 정보
        ItemImgVO vo = ItemImgVO.builder()
                .itemId(4L)
                .imgName("test 이미지")
                .oriImgName("test 원본 이미지명")
                .imgUrl("test URL")
                .repImgYn("Y")
                .build();

        assertNull(vo.getItemImgId(), "ItemImgId가 NULL이 아닙니다.");

        itemImgMapper.insertItemImg(vo);

        assertNotNull(vo.getItemImgId(), "ItemImgId가 NULL 값 입니다.");
        log.info("ItemImgId >> {}", vo.getItemImgId());
    }

    @Test
    @DisplayName("상품 이미지 수정")
    public void testUpdateImg(){

        //given : 수정할 이미지 정보 생성
        ItemImgVO vo = ItemImgVO.builder()
                .itemImgId(2L)
                .imgName("수정 이미지3")
                .imgUrl("수정 url3")
                .oriImgName("수정 ori_name3")
                .repImgYn("Y")
                .itemId(3L)
                .build();

        //when : 이미지 수정 요청
        int result = itemImgMapper.updateItemImg(vo);

        //then : 결과 검증
        log.info("update result >> {}", result);
    }

    @Test
    @DisplayName("상품 이미지 단건 조회")
    public void testGetItemImg(){

        //given : 조회할 상품 이미지 Id
        Long itemImgId = 3L;

        //when : 상품 단건 조회
        ItemImgVO itemImg = itemImgMapper.getItemImgById(itemImgId);

        //then : 결과 겸증
        assertNotNull(itemImg, "ID에 해당하는 상품 이미지가 존재하지 않습니다.");

        log.info("itemImg >> {}", itemImg);
    }

    @Test
    @DisplayName("상품 이미지 삭제")
    public void testDelete(){

        //given : 삭제할 상품 Id
        Long itemId = 3L;

        //when : 상품 이미지 삭제
        itemImgMapper.deleteItemImg(itemId);

        //then : 결과 검증
        List<ItemImgVO> deleteImgs = itemImgMapper.getItemList(itemId);
        assertTrue(deleteImgs.isEmpty(), "이미지가 삭제되지 않았습니다.");
    }

}
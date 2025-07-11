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
 */

@SpringBootTest
@Slf4j
class ItemImgMapperTest {

    @Autowired
    private ItemImgMapper mapper;

    @Test
    @DisplayName("상품 이미지 전체 조회")
    public void testGetList(){
        List<ItemImgVO> list = mapper.getItemList(1L);

        for(ItemImgVO vo : list){
            log.info("list : {}", vo);
        }

        if (list.isEmpty()){
            log.info("ItemImg에 데이터 없음");
        }
    }

}
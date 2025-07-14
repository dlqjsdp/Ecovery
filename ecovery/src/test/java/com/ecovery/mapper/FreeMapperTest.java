package com.ecovery.mapper;

import com.ecovery.constant.ItemCondition;
import com.ecovery.domain.FreeVO;
import jdk.dynalink.linker.support.Guards;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class FreeMapperTest {

    @Autowired
    private FreeMapper freeMapper;

    @Test
    @DisplayName("게시글 등록 테스트")
    @Transactional
    public void testInsert() {

        // Given (준비)
        FreeVO vo = new FreeVO();
        vo.setTitle("테스트 제목");
        vo.setMemberId(1L);
        vo.setCategory("가구");
        vo.setRegionGu("강남구");
        vo.setRegionDong("역삼동");
        vo.setContent("테스트 내용");
        vo.setItemCondition(ItemCondition.HIGH);

        // When (실행)
        freeMapper.insertSelectKey(vo);

        // Then (검증)
        // 1) insert 후 FreeId가 자동으로 채워졌는지
        assertNotNull(vo.getFreeId(), "id는 null값이면 안됩니다.");

        // 2) 방금 insert한 데이터 조회 (PK로 단건 조회)
        FreeVO inserted = freeMapper.read(vo.getFreeId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) DB에 저장된 데이터가 내가 입력한 값이랑 같은지 검증 (예상괎과 실제값 비교)
        assertEquals("테스트 제목", inserted.getTitle());
        assertEquals("테스트 내용", inserted.getContent());

        log.info("삽입된 게시글 : {}", inserted);
    }

}
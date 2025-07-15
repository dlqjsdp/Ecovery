package com.ecovery.mapper;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import com.ecovery.domain.FreeVO;
import com.ecovery.dto.FreeDto;
import jdk.dynalink.linker.support.Guards;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 무료나눔 게시글 Mapper 테스트
 * FreeMapper의 게시글 CRUD 기능이 정상 동작하는지 단위 테스트 진행
 * 게시글 등록, 조회(단건/전체), 수정, 삭제 기능 검증
 * @author : yeonsu
 * @fileName : FreeMapperTest
 * @since : 250715
 */

@SpringBootTest
@Slf4j
class FreeMapperTest {

    @Autowired
    private FreeMapper freeMapper;

    @Test
    @DisplayName("게시글 등록 테스트")
    public void testInsert() {

        // Given (준비)
        FreeVO vo = new FreeVO();
        vo.setTitle("세번째");
        vo.setMemberId(1L);
        vo.setCategory("잡화");
        vo.setRegionGu("마포구");
        vo.setRegionDong("상암동");
        vo.setContent("세번째 폐기물");
        vo.setItemCondition(ItemCondition.LOW);

        // When (실행)
        freeMapper.insertSelectKey(vo);

        // Then (검증)
        // 1) insert 후 FreeId가 자동으로 채워졌는지
        assertNotNull(vo.getFreeId(), "id는 null값이면 안됩니다.");

        // 2) 방금 insert한 데이터 조회 (PK로 단건 조회)
        FreeVO inserted = freeMapper.read(vo.getFreeId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) DB에 저장된 데이터가 내가 입력한 값이랑 같은지 검증 (예상괎과 실제값 비교)
        assertEquals("세번째", inserted.getTitle());
        assertEquals("세번째 폐기물", inserted.getContent());


        log.info("삽입된 게시글 : {}", inserted);
    }

    @Test
    @DisplayName("게시글 전체 목록 조회")
    public void testGetList(){
        List<FreeVO> list = freeMapper.getFreeList();
        log.info("전체 게시글 수 : {}", list.size());

        for (FreeVO freeVO : list) {
            log.info("게시글 : {}", freeVO);
        }
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void testRead(){
        Long targetId = 2L;
        FreeVO freeVO = freeMapper.read(targetId);
        if (freeVO != null) {
            log.info(freeVO.toString());
        }else {
            log.info("조회 결과가 없습니다.");
        }
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    public void testDelete(){

        //Given : 삭제할 게시글 ID가 존재함
        Long targetId = 5L;

        //When : 게시글 삭제 요청 실행
        int deletedCount = freeMapper.delete(targetId);

        //Then : 삭제 결과 확인
        if (deletedCount == 1) {
            log.info("게시글 삭제 성공 : {}", targetId);
        }else {
            log.info("게시글 삭제 실패 또는 존재하지 않음 : {}", targetId);
        }
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    public void testUpdate(){

        // Given : 수정할 게시글 정보 준비
        FreeVO vo = new FreeVO();
        vo.setFreeId(2L);  // 수정 대상 ID
        vo.setTitle("수정된 제목");
        vo.setContent("수정된 내용");
        vo.setCategory("가전");
        vo.setRegionGu("서초구");
        vo.setRegionDong("반포동");
        vo.setItemCondition(ItemCondition.HIGH);
        vo.setDealStatus(DealStatus.DONE);

        // When: 수정 메서드 실행
        int updatedCount = freeMapper.update(vo);

        // Then: 결과 확인
        if (updatedCount == 1) {
            log.info("게시글 수정 성공: {}", vo);
        } else {
            log.info("게시글 수정 실패: {}", vo);
        }
    }
}





























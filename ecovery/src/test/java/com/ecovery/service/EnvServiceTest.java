package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 환경톡톡 게시글 Service 테스트
 * Service 계층의 CRUD 기능 단위 테스트 클래스
 * @author : yukyeong
 * @fileName : EnvServiceTest.java
 * @since : 250715
 * @history
     - 250715 | yukyeong | 게시글 등록, 단건 조회, 수정, 삭제 테스트 작성
     - 250716 | yukyeong | 게시글 목록 조회 (페이징 포함), 게시글 총 개수 조회, 조회수 증가 테스트 작성
 */

@SpringBootTest
@Slf4j
class EnvServiceTest {

    @Autowired
    private EnvService envService;

    @Test
    @DisplayName("게시글 등록 테스트")
    @Transactional
    public void testRegister(){

        // Given
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L);
        vo.setTitle("서비스 등록 테스트 제목");
        vo.setContent("서비스 등록 테스트 내용");

        // When
        envService.register(vo);

        // Then
        // 1) insert 후 envId가 자동으로 채워졌는지 (useGeneratedKeys가 정상동작했는지 확인)
        assertNotNull(vo.getEnvId(), "id는 null값이면 안됩니다.");

        // 2) 방금 insert한 데이터 조회 (PK로 단건 조회)
        EnvVO inserted = envService.get(vo.getEnvId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) DB에 저장된 데이터가 내가 입력한 값과 같은지 검증 (예상값과 실제값 비교)
        assertEquals("서비스 등록 테스트 제목", inserted.getTitle());
        assertEquals("서비스 등록 테스트 내용", inserted.getContent());

        log.info("삽입된 게시글: {}", inserted);

    }


    @Test
    @DisplayName("게시글 단건 조회")
    public void testGet() {
        // Given
        Long targetId = 1L;

        // When
        EnvVO vo = envService.get(targetId);

        //Then
        assertNotNull(vo, "조회 결과가 null이면 안됨.");
        log.info("조회된 게시글 : {}", vo);
    }


    @Test
    @DisplayName("게시글 수정 테스트")
    @Transactional
    public void testModify() {
        // Given
        // 1) 더미 데이터 생성
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L); // 작성자 ID
        vo.setTitle("수정 테스트 제목1"); // 초기 제목
        vo.setContent("수정 테스트 내용1"); // 초기 내용
        // 2) 게시글 등록
        envService.register(vo);
        // 3) 등록된 게시글의 ID 확인 (PK)
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다."); // 등록된 게시글이 잘 등록되었는지

        // When : 게시글 제목과 내용 수정
        // 1) 게시글 제목과 내용을 새로운 값으로 수정
        vo.setTitle("수정된 제목");
        vo.setContent("수정된 내용");
        // 2) 수정 메서드 호출
        boolean result = envService.modify(vo);

        // Then
        // 1) 수정 결과가 true(수정 성공)인지 확인
        assertTrue(result, "수정결과가 false면 안됨.");

        // 2) 수정된 게시글을 다시 조회
        EnvVO updated = envService.get(insertedId);
        // 3) 조회 결과가 null이 아닌지 검증
        assertNotNull(updated, "수정 후 결과가 null이면 안됩니다.");
        // 4) 제목과 내용이 수정되었는지 검증
        assertEquals("수정된 제목", updated.getTitle());
        assertEquals("수정된 내용", updated.getContent());

        log.info("수정된 게시글: {}", updated);

    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @Transactional
    public void testRemove() {
        // Given
        // 1) 테스트용 더미 데이터 생성
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L);
        vo.setTitle("삭제 테스트 제목");
        vo.setContent("삭제 테스트 내용");
        // 2) 게시글 등록
        envService.register(vo);
        // 3) 등록된 게시글의 ID 가져오기
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됨");

        // When
        // 1) 게시글 삭제
        boolean result = envService.remove(vo.getEnvId());

        // Then
        // 1) 삭제가 성공했는지 확인
        assertTrue(result, "삭제 결과가 false면 실패.");
        // 2) 삭제 후 조회 시 null값이 나오는지 확인
        EnvVO deleted = envService.get(insertedId);
        assertNull(deleted, "삭제 후 조회결과는 null.");

        log.info("삭제된 게시글 ID: {}", insertedId);

    }

    @Test
    @DisplayName("게시글 페이징 + 검색 목록 조회 테스트")
    @Transactional
    public void testGetList(){
        // Given
        Criteria cri = new Criteria();
        cri.setPageNum(1); // 1페이지
        cri.setAmount(10); // 한 페이지에 10건
        cri.setType("T"); // 제목 검색
        cri.setKeyword("테스트"); // 검색어

        // When
        List<EnvVO> list = envService.getList(cri);

        // Thwn
        assertNotNull(list, "조회 결과가 null값이면 안됨");
        log.info("조회된 게시글 수 : {}", list.size());

        for (EnvVO vo : list) {
            log.info("게시글: {}", vo);
        }
    }

    @Test
    @DisplayName("게시글 전체 개수 조회 테스트(검색 조건 포함)")
    @Transactional
    public void testGetTotal(){
        // Given
        Criteria cri = new Criteria();
        cri.setType("T"); // T = 제목
        cri.setKeyword("테스트"); // 검색어

        // When
        int totalCount = envService.getTotal(cri);

        // Then
        log.info("검색 조건에 해당하는 전체 게시글 수 : {}", totalCount);
        assertTrue(totalCount >= 0, "게시글 수는 0 이상");
    }

    @Test
    @DisplayName("게시글 조회수 증가 테스트")
    @Transactional
    public void testUpdateViewCount(){
        // Given
        // 1) 테스트용 데이터 생성
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L); // 작성자 ID 설정
        vo.setTitle("조회수 증가 테스트 제목");
        vo.setContent("조회수 증가 테스트 내용");
        // 2) 게시글 등록
        envService.register(vo);
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다.");

        // When
        // 1) 조회수 증가 전 값 조회
        EnvVO before = envService.get(insertedId);
        int beforeCount = before.getViewCount(); // 증가 전 조회수 값
        log.info("조회수 증가 전: {}", beforeCount);
        // 2) 조회수 1 증가 실행
        envService.increaseViewCount(insertedId);

        // Then
        // 1) 조회수 1 증가 후 조회
        EnvVO after = envService.get(insertedId);
        int afterCount = after.getViewCount(); // 증가 후 조회수 값

        log.info("조회수 증가 후: {}", afterCount);

        // 2) 증가된 값이 예상대로 1 증가했는지 검증
        assertEquals(beforeCount + 1, afterCount, "조회수는 1 증가");
    }


}
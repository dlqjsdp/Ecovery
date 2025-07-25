package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
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
 * @fileName : EnvServiceTest
 * @since : 250715
 * @history
     - 250715 | yukyeong | 게시글 등록, 단건 조회, 수정, 삭제 테스트 작성
     - 250716 | yukyeong | 게시글 목록 조회 (페이징 포함), 게시글 총 개수 조회, 조회수 증가 테스트 작성
     - 250718 | yukyeong | 테스트 전반적으로 EnvDto 기반으로 통일 및 로그 개선
     - 250725 | yukyeong | 게시글 등록/수정 테스트에 category 필드 추가 및 검증
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

        // Given - 테스트용 게시글 DTO 생성 및 값 세팅
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("서비스 등록 테스트 제목");
        envDto.setContent("서비스 등록 테스트 내용");
        envDto.setCategory("news"); // 카테고리: 환경 뉴스


        // When - 게시글 등록 (DTO → VO 변환 후 insert 수행)
        envService.register(envDto);

        // Then
        // 1) insert 후 자동 생성된 envId가 DTO에 세팅되었는지 확인
        assertNotNull(envDto.getEnvId(), "id는 null값이면 안됩니다.");

        // 2) 방금 등록한 게시글을 ID로 다시 조회 (DTO 반환)
        EnvDto inserted = envService.get(envDto.getEnvId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) 등록한 제목과 내용이 DB에서 정상적으로 조회되는지 확인
        assertEquals("서비스 등록 테스트 제목", inserted.getTitle());
        assertEquals("서비스 등록 테스트 내용", inserted.getContent());
        assertEquals("news", inserted.getCategory()); // 카테고리 검증

        log.info("삽입된 게시글: {}", inserted);

    }


    @Test
    @DisplayName("게시글 단건 조회")
    public void testGet() {
        // Given - 조회할 게시글의 ID 지정
        Long targetId = 1L;

        // When - 해당 ID로 게시글 단건 조회 (DTO 반환)
        EnvDto envDto = envService.get(targetId);

        //Then
        assertNotNull(envDto, "조회 결과가 null이면 안됨.");
        // 제목과 내용 확인
        log.info("조회된 게시글 제목 : {}", envDto.getTitle());
        log.info("조회된 게시글 내용 : {}", envDto.getContent());;
    }


    @Test
    @DisplayName("게시글 수정 테스트")
    @Transactional
    public void testModify() {
        // Given
        // 테스트용 게시글 DTO 생성 및 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L); // 작성자 ID
        envDto.setTitle("수정 테스트 제목1"); // 초기 제목
        envDto.setContent("수정 테스트 내용1"); // 초기 내용
        envDto.setCategory("tips"); // 초기 카테고리: 환경 팁

        envService.register(envDto); // 게시글 등록
        Long insertedId = envDto.getEnvId(); // 등록된 게시글의 ID 확인 (PK)
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다."); // 등록된 게시글이 잘 등록되었는지

        // When
        // 1) 제목과 내용을 수정
        envDto.setTitle("수정된 제목");
        envDto.setContent("수정된 내용");
        envDto.setCategory("issue"); // 수정된 카테고리: 주간 이슈

        // 2) 수정 메서드 호출
        boolean result = envService.modify(envDto);

        // Then
        // 1) 수정 결과가 true(수정 성공)인지 확인
        assertTrue(result, "수정결과가 false면 안됨.");
        // 2) 수정된 게시글을 다시 조회
        EnvDto updated = envService.get(insertedId);
        // 3) 조회 결과가 null이 아닌지 검증
        assertNotNull(updated, "수정 후 결과가 null이면 안됩니다.");
        // 4) 제목과 내용이 수정되었는지 검증
        assertEquals("수정된 제목", updated.getTitle());
        assertEquals("수정된 내용", updated.getContent());
        assertEquals("issue", updated.getCategory()); // 카테고리 검증

        log.info("수정된 게시글: {}", updated);

    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @Transactional
    public void testRemove() {
        // Given
        // 테스트용 게시글 DTO 생성 및 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("삭제 테스트 제목");
        envDto.setContent("삭제 테스트 내용");

        envService.register(envDto); // 게시글 등록
        Long insertedId = envDto.getEnvId(); // 등록된 게시글의 ID 가져오기
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됨");

        // When - 게시글 삭제
        boolean result = envService.remove(insertedId);

        // Then
        // 1) 삭제되었는지 확인
        assertTrue(result, "삭제 결과가 false면 실패.");
        // 2) 삭제된 게시글을 다시 조회했을 때 null값이 나오는지 확인
        EnvDto deleted = envService.get(insertedId);
        assertNull(deleted, "삭제 후 조회결과는 null.");

        log.info("삭제된 게시글 ID: {}", insertedId);

    }

    @Test
    @DisplayName("게시글 페이징 + 검색 목록 조회 테스트")
    @Transactional
    public void testGetList(){
        // Given - 검색 조건과 페이징 정보 세팅
        Criteria cri = new Criteria();
        cri.setPageNum(1); // 1페이지
        cri.setAmount(10); // 한 페이지에 10건
        cri.setType("T"); // T = 제목 검색
        cri.setKeyword("게시글"); // 검색어

        // When - 조건에 맞는 게시글 목록 조회 (DTO 리스트 반환)
        List<EnvDto> list = envService.getList(cri);

        // Thwn
        assertNotNull(list, "조회 결과가 null값이면 안됨");
        log.info("조회된 게시글 수 : {}", list.size());

        for (EnvDto envDto : list) {
            log.info("게시글: {}", envDto);
        }
    }

    @Test
    @DisplayName("게시글 전체 개수 조회 테스트(검색 조건 포함)")
    @Transactional
    public void testGetTotal(){
        // Given
        Criteria cri = new Criteria();
        cri.setType("T"); // T = 제목
        cri.setKeyword("게시글"); // 검색어

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
        // 1) 테스트용 DTO 객체 생성
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L); // 작성자 ID 설정
        envDto.setTitle("조회수 증가 테스트 제목");
        envDto.setContent("조회수 증가 테스트 내용");
        // 2) 게시글 등록
        envService.register(envDto);
        Long insertedId = envDto.getEnvId(); // 등록된 ID 확인
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다.");

        // When
        // 1) 조회수 증가 전 값 조회
        EnvDto before = envService.get(insertedId);
        int beforeCount = before.getViewCount(); // 증가 전 조회수 값
        log.info("조회수 증가 전: {}", beforeCount);
        // 2) 조회수 1 증가 실행
        envService.increaseViewCount(insertedId);

        // Then
        // 1) 조회수 1 증가 후 조회
        EnvDto after = envService.get(insertedId);
        int afterCount = after.getViewCount(); // 증가 후 조회수 값

        log.info("조회수 증가 후: {}", afterCount);

        // 2) 증가된 값이 예상대로 1 증가했는지 검증
        assertEquals(beforeCount + 1, afterCount, "조회수는 1 증가");
    }


}
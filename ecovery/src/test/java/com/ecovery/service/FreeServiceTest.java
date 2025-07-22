//package com.ecovery.service;
//
//import com.ecovery.constant.DealStatus;
//import com.ecovery.constant.ItemCondition;
//import com.ecovery.domain.FreeVO;
//import com.ecovery.dto.Criteria;
//import com.ecovery.dto.FreeDto;
//import com.ecovery.mapper.FreeMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Slf4j
//class FreeServiceTest {
//
//    @Autowired
//    private FreeService freeService;
//
//    @Autowired
//    private FreeImgService freeImgService;
//
//    @Test
//    @DisplayName("게시글 등록 테스트")
//    @Transactional
//    public void testRegister(){
//
//        // Given
//        FreeVO vo = FreeVO.builder()
//                .title("service 테스트")
//                .content("service 내용")
//                .memberId(1L)
//                .category("가구")
//                .regionGu("강서구")
//                .regionDong("등촌동")
//                .itemCondition(ItemCondition.HIGH)
//                .build();
//
//        // When
//        freeService.register(vo);
//
//        // Then
//        assertThat(vo.getFreeId()).isNotNull();
//        log.info("등록된 게시글 ID : {}", vo.getFreeId());
//    }
//
//    @Test
//    @DisplayName("게시글 단건 조회 테스트")
//    @Transactional
//    public void testGet() {
//        // Given: 임시 데이터 먼저 등록
//        FreeVO vo = FreeVO.builder()
//                .title("조회 테스트")
//                .content("조회 내용")
//                .memberId(2L)
//                .category("도서")
//                .regionGu("관악구")
//                .regionDong("봉천동")
//                .itemCondition(ItemCondition.MEDIUM)
//                .dealStatus(DealStatus.DONE)
//                .build();
//        freeService.register(vo);
//
//        // When
//        FreeDto result = freeService.get(vo.getFreeId());
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getTitle()).isEqualTo("조회 테스트");
//        log.info("조회된 게시글: {}", result);
//    }
//
//    @Test
//    @DisplayName("게시글 전체 목록 조회 테스트")
//    public void testGetAll() {
//        Criteria cri = new Criteria(1, 10); // 1페이지, 10개씩
//        List<FreeDto> list = freeService.getAll(cri);
//
//        assertThat(list).isNotNull();
//        assertThat(list.size()).isGreaterThanOrEqualTo(0); // 0 이상이면 성공
//        list.forEach(dto -> log.info("게시글: {}", dto));
//    }
//
//    @Test
//    @DisplayName("게시글 수정 테스트")
//    @Transactional
//    public void testModify() {
//        // Given: 먼저 등록
//        FreeVO vo = FreeVO.builder()
//                .title("수정 전 제목")
//                .content("수정 전 내용")
//                .memberId(2L)
//                .category("기타")
//                .regionGu("중구")
//                .regionDong("명동")
//                .itemCondition(ItemCondition.LOW)
//                .dealStatus(DealStatus.DONE)
//                .build();
//        freeService.register(vo);
//
//        // When: 수정 내용 설정
//        vo.setTitle("수정된 제목");
//        vo.setContent("수정된 내용");
//        vo.setDealStatus(DealStatus.DONE);
//
//        freeService.modify(vo);
//
//        // Then
//        FreeDto updated = freeService.get(vo.getFreeId());
//        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
//        assertThat(updated.getContent()).isEqualTo("수정된 내용");
//        log.info("수정된 게시글: {}", updated);
//    }
//
//
//    @Test
//    @DisplayName("게시글 삭제 테스트")
//    @Transactional
//    public void testRemove() {
//
//        // Given : 먼저 게시글을 등록
//        FreeVO vo = FreeVO.builder()
//                .title("삭제 테스트 제목")
//                .content("삭제 테스트 내용")
//                .memberId(2L)
//                .category("생활")
//                .regionGu("강남구")
//                .regionDong("논현동")
//                .itemCondition(ItemCondition.MEDIUM)
//                .dealStatus(DealStatus.DONE)
//                .build();
//        freeService.register(vo);
//
//        Long id = vo.getFreeId(); // 등록된 게시글 ID
//
//        // When: 삭제 시도
//        boolean isRemoved = freeService.remove(vo);
//
//        // Then: 결과가 true인지 확인 + 삭제된 게시글이 조회되지 않아야 함
//        assertThat(isRemoved).isTrue();
//
//        FreeDto deleted = freeService.get(id);
//        assertThat(deleted).isNull(); // 삭제된 경우 null이어야 함
//
//        log.info("삭제 성공 여부: {}, 삭제 대상 ID: {}", isRemoved, id);
//    }
//
//    @Test
//    @DisplayName("전체 게시글 수 조회 테스트")
//    public void testGetTotalCount() {
//        Criteria cri = new Criteria(1, 10);
//        int totalCount = freeService.getTotalCount(cri);
//
//        assertThat(totalCount).isGreaterThanOrEqualTo(0); // 게시글이 하나도 없어도 0 이상
//        log.info("전체 게시글 수: {}", totalCount);
//    }
//
//    @Test
//    @DisplayName("조회수 증가 테스트")
//    @Transactional
//    public void testUpdateViewCount() {
//        // Given: 게시글 등록
//        FreeVO vo = FreeVO.builder()
//                .title("조회수 테스트")
//                .content("조회수 증가 전")
//                .memberId(2L)
//                .category("전자제품")
//                .regionGu("성동구")
//                .regionDong("성수동")
//                .itemCondition(ItemCondition.HIGH)
//                .build();
//        freeService.register(vo);
//
//        Long freeId = vo.getFreeId();
//
//        // When: 조회수 증가 전 값 저장 + 1회 증가
//        FreeDto before = freeService.get(freeId);
//        int prevViewCount = before.getViewCount();
//
//        freeService.updateViewCount(freeId);
//
//        // Then: 증가 후 값 확인
//        FreeDto after = freeService.get(freeId);
//        assertThat(after.getViewCount()).isEqualTo(prevViewCount + 1);
//        log.info("조회수 변경: {} -> {}", prevViewCount, after.getViewCount());
//    }
//
//}
//

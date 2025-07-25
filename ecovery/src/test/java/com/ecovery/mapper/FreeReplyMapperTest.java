package com.ecovery.mapper;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.FreeReplyDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/*
 * 무료나눔 댓글 Mapper 테스트
 * FreeReplyMapper의 댓글 및 대댓글 crud 기능 정상작동 여부 단위테스트
 *
 * 일반 댓글 및 대댓글 등록 테스트
 * 특정 댓글 및 게시글 전체 댓글 수 조회 테스트
 * 댓글/대댓글 수정 테스트
 * 댓글 삭제시 Cascade 설정을 통한 대댓글 자동 삭제 테스트 포함
 *
 * @author : yeonsu
 * @fileName : FreeReplyMapperTest
 * @since : 250716
 */

@SpringBootTest
@Slf4j
class FreeReplyMapperTest {

    @Autowired
    private FreeReplyMapper freeReplyMapper;

    @Test
    @DisplayName("일반 댓글 등록 테스트")
    public void testInsertReply() {

        // Given : 새로 등록할 댓글 정보 생성
        FreeReplyVO vo = FreeReplyVO.builder()
                .freeId(7L) // 게시글 ID
                .memberId(2L) // 작성자 ID
                .content("추가됐니")
                .parentId(null) // 일반 댓글 (대댓글은 부모ID 입력)
                .build();
        // createdAT은 DB에서 자동 처리

        // When : 댓글 등록 메소드 호출
        freeReplyMapper.insert(vo);     // Mapper 호출

        // Then : 결과확인 (replyId가 생성되었는지 확인)
        assertNotNull(vo.getReplyId(), "댓글ID가 null이면 안됩니다.");
        log.info("삽입된 댓글 ID = {}", vo.getReplyId()); // @Options가 설정된 경우에만 값 확인 가능
    }

    @Test
    @DisplayName("대댓글 등록 테스트")
    public void testInsertChildReply() {

        // Given : 대댓글 정보를 생성 (free_id, member_id, content, parent_id 필수)
        FreeReplyVO childReply = FreeReplyVO.builder()
                .freeId(7L)   // 대댓글이 달릴 게시글 ID
                .memberId(2L) // 작성자 ID
                .content("대댓글2 테스트")
                .parentId(6L) // 대댓글이 달릴 부모 댓글 ID (reply_id)
                .build();

        // When : 대댓글 등록
        freeReplyMapper.insert(childReply);

        // Then : 대댓글 ID가 정상 생성되었는지 확인
        assertNotNull(childReply.getReplyId(), "대댓글 ID가 null이면 안됩니다.");
        log.info("등록된 대댓글 ID = {}", childReply.getReplyId());
    }

    @Test
    @DisplayName("일반 댓글 조회 테스트")
    public void testGetReply() {

        // Given : 특정 게시글 ID에 댓글이 1개 이상 있다고 가정
        Long replyId = 4L;

        // When : 게시글 ID로 댓글 목록 조회
        FreeReplyDto list = freeReplyMapper.read(replyId);

        // Then : 결과 검증
        assertNotNull(list, "댓글이 존재해야 합니다.");
        assertEquals(replyId, list.getReplyId(), "조회된 댓글 ID가 일치해야 합니다.");

        log.info("조회된 댓글 정보 : {}", list);
    }

    @Test
    @DisplayName("특정 게시글 댓글 수 조회 테스트 ")
    public void testGetTotalReplyCount() {

        // Given : 댓글 수를 조회할 게시글 ID
        Long freeId = 4L;

        // When : 해당 게시글의 전체 댓글 수 조회
        int totalCount = freeReplyMapper.getTotalReplyCount(freeId);

        // Then : 결과 출력
        if (totalCount > 0) {
            log.info("총 댓글 수 : {}", totalCount);
        } else {
            log.info("해당 게시글에는 댓글이 없습니다.");
        }
    }

    @Test
    @DisplayName("일반 댓글 수정 테스트")
    public void testUpdateReply() {

        // Given : 이미 존재하는 댓글 ID
        Long replyId = 6L;

        // When : 해당 댓글 내용을 수정
        FreeReplyVO vo = FreeReplyVO.builder()
                .replyId(replyId)
                .content("수정됐니")
                .build();
        int Count = freeReplyMapper.update(vo);

        // Then : 수정 결과 확인
        assertEquals(1, Count, "수정된 행이 1개여야 합니다");

        // 수정된 내용 확인
        FreeReplyDto dto = freeReplyMapper.read(replyId);
        assertEquals("수정됐니", dto.getContent());
        log.info("수정된 댓글 내용 : {}", dto.getContent());
    }

    @Test
    @DisplayName("대댓글 수정 테스트")
    public void testGetChildReply() {

        // Given : 수정할 대댓글 ID
        Long replyId = 7L;

        // When : 해당 대댓글 내용 수정
        FreeReplyVO vo = FreeReplyVO.builder()
                .replyId(replyId)
                .content("대댓글 수정")
                .build();
        int result = freeReplyMapper.update(vo);

        // Then : 수정 성공 여부 및 내용 확인
        assertEquals(1, result, "수정된 행 수는 1개여야 합니다.");

        FreeReplyDto dto = freeReplyMapper.read(replyId);
        assertEquals("대댓글 수정", dto.getContent());
        log.info("수정된 대댓글 내용 : {}", dto.getContent());

    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    public void testDeleteReply() {

        // Given : 삭제할 댓글 ID가 존재함
        Long replyId = 6L;

        // When : 댓글이 부모댓글이라면 대댓글 수 확인 후 삭제
        int childCount = freeReplyMapper.getChildReplyCount(replyId);
        int deletedCount = freeReplyMapper.delete(replyId); // 부모만 삭제(대댓글은 cascade로 자동삭제)

        // Then : 삭제 결과 확인
        log.info("삭제된 대댓글 수 : {}", childCount);
        log.info("총 삭제된 댓글 수 : {}", childCount + deletedCount);

        if (deletedCount > 0) {
            log.info("댓글 삭제 테스트 성공 ");
        } else {
            log.info(" 댓글 삭제 테스트 실패: 댓글이 존재하지 않음", replyId);
        }


    }

}


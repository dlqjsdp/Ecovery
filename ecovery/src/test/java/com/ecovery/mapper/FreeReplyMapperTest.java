//package com.ecovery.mapper;
//
//import com.ecovery.domain.FreeReplyVO;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Slf4j
//class FreeReplyMapperTest {
//
//    @Autowired
//    private FreeReplyMapper freeReplyMapper;
//
//    @Test
//    public void testInsertReply() {
//        FreeReplyVO vo = new FreeReplyVO();
//        vo.setFreeId(1L);               // 게시글 ID
//        vo.setMemberId(2L);             // 작성자 ID
//        vo.setContent("댓글 테스트");
//        vo.setParentId(null);           // 일반 댓글 (null) 또는 대댓글일 경우 부모 ID
//        // createdAt은 DB에서 NOW()로 자동 처리
//
//        freeReplyMapper.insert(vo);     // Mapper 호출
//
//        log.info("삽입된 댓글 ID = {}", vo.getReplyId()); // @Options가 설정된 경우에만 값 확인 가능
//    }
//}
//

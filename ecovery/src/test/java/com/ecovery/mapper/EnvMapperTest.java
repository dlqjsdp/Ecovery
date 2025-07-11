package com.ecovery.mapper;

import com.ecovery.domain.EnvVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 환경톡톡 게시글 Mapper 테스트
 * Mapper 기능 단위 테스트 클래스
 * @author : yukyeong
 * @fileName : EnvMapperTest.java
 * @since : 250711
 * @history
     - 250711 | yukyeong | 게시글 전체 목록 조회, 단건 조회 테스트 작성
 */

@SpringBootTest
@Slf4j
class EnvMapperTest {

    @Autowired
    private EnvMapper envMapper;

    @Test
    @DisplayName("게시글 전체 목록 조회")
    public void testGetList() {
        List<EnvVO> list = envMapper.getList();
        log.info("전체 게시글 수: {}", list.size());

        for (EnvVO vo : list) {
            log.info("게시글: {}", vo);
        }
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void testRead() {
        Long targetId = 1L; // 조회할 게시글 ID
        EnvVO vo = envMapper.read(targetId);
        if (vo != null) {
            log.info(vo.toString());
        } else {
            log.info("조회 결과가 없습니다.");
        }
    }

}
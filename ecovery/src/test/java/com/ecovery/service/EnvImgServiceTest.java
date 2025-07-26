package com.ecovery.service;

import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvImgDto;
import com.ecovery.mapper.EnvImgMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EnvImgServiceTest
 * - 환경톡톡 게시판의 이미지 관련 기능(등록, 조회, 삭제)에 대한 단위 테스트 클래스
 * - EnvImgService 구현체에 대한 통합 테스트 수행
 * - 테스트를 위해 게시글(EnvDto)과 이미지(EnvImgDto)를 함께 사용
 *
 * @author   : yukyeong
 * @fileName : EnvImgServiceTest.java
 * @since    : 2025-07-26
 * @history
    - 2025-07-26 | yukyeong | 이미지 등록, 목록 조회, 단일 삭제, 전체 삭제 테스트 구현
 */

@SpringBootTest
@Slf4j
class EnvImgServiceTest {

    @Autowired
    private EnvImgService envImgService; // 테스트 대상 서비스

    @Autowired
    private EnvService envService; // 테스트 대상 서비스

    @Autowired
    private EnvImgMapper envImgMapper; // 게시글 등록용 서비스


    @Test
    @DisplayName("이미지 등록 테스트")
    @Transactional
    public void testRegister() throws Exception{
        // Given - 1. 테스트용 게시글 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("이미지 테스트 게시글");
        envDto.setContent("이미지 등록 전용 게시글");
        envDto.setCategory("tip");
        envService.register(envDto);
        Long envId = envDto.getEnvId();
        assertNotNull(envId);

        // Given - 2. 테스트용 이미지 DTO 생성
        EnvImgDto imgDto = EnvImgDto.builder()
                .envId(envId)
                .build();

        MockMultipartFile mockFile = new MockMultipartFile(
                "imgFile",
                "sample.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        // When - 이미지 등록
        envImgService.register(imgDto, mockFile); // 이미지 등록 수행

        // Then - 해당 게시글 ID로 이미지 리스트를 조회해서 검증
        List<EnvImgDto> resultList = envImgService.getListByEnvId(envId);
        assertEquals(1, resultList.size());
        assertEquals("sample.jpg", resultList.get(0).getOriImgName());
        assertEquals(envId, resultList.get(0).getEnvId());
        log.info("등록된 이미지 정보: {}", resultList.get(0));

        log.info("등록된 이미지 정보: {}", resultList.get(0));
    }

    @Test
    @DisplayName("이미지 목록 조회 테스트")
    @Transactional
    public void testGetListByEnvId() throws Exception{
        // Given - 1. 테스트용 게시글 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("이미지 목록 테스트 게시글");
        envDto.setContent("여러 이미지를 등록합니다.");
        envDto.setCategory("news");
        envService.register(envDto);
        Long envId = envDto.getEnvId();
        assertNotNull(envId);

        // Given - 2. 이미지 2건 등록
        for (int i = 1; i <= 2; i++) {
            EnvImgDto dto = EnvImgDto.builder().envId(envId).build();
            MockMultipartFile mockFile = new MockMultipartFile(
                    "imgFile",
                    "img" + i + ".jpg",
                    "image/jpeg",
                    ("fake image content " + i).getBytes()
            );
            envImgService.register(dto, mockFile);
        }

        // When - 이미지 목록 조회
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);

        // Then - 이미지 2건이 조회되고, 파일명이 일치해야 함
        assertEquals(2, imgList.size());
        assertTrue(imgList.stream().anyMatch(img -> img.getOriImgName().equals("img1.jpg")));
        assertTrue(imgList.stream().anyMatch(img -> img.getOriImgName().equals("img2.jpg")));
        log.info("조회된 이미지 목록: {}", imgList);
    }

    @Test
    @DisplayName("단일 이미지 삭제 테스트")
    @Transactional
    public void testDeleteById() throws Exception {
        // Given - 1. 테스트용 게시글 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("삭제 테스트 게시글");
        envDto.setContent("삭제용 이미지 등록");
        envDto.setCategory("event");
        envService.register(envDto);
        Long envId = envDto.getEnvId();
        assertNotNull(envId);

        // Given - 2. 이미지 등록
        EnvImgDto dto = EnvImgDto.builder().envId(envId).build();
        MockMultipartFile mockFile = new MockMultipartFile(
                "imgFile", "delete.jpg", "image/jpeg", "fake delete image".getBytes()
        );
        envImgService.register(dto, mockFile);

        // 등록된 이미지 목록에서 ID 확인
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);
        assertEquals(1, imgList.size());
        Long envImgId = imgList.get(0).getEnvImgId();

        // When - 단일 이미지 삭제 수행
        boolean deleted = envImgService.deleteById(envImgId);

        // Then - 삭제 성공 여부 확인 및 목록 비어 있는지 확인
        assertTrue(deleted);

        List<EnvImgDto> afterDeleteList = envImgService.getListByEnvId(envId);
        assertEquals(0, afterDeleteList.size());
        log.info("단일 이미지 삭제 성공. 이미지 ID: {}", envImgId);
    }

    @Test
    @DisplayName("게시글 ID로 이미지 전체 삭제 테스트")
    @Transactional
    public void testDeleteByEnvId() throws Exception{
        // Given - 1. 테스트용 게시글 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("전체 삭제 테스트 게시글");
        envDto.setContent("이미지 여러 개 등록 후 전체 삭제 테스트");
        envDto.setCategory("news");
        envService.register(envDto);
        Long envId = envDto.getEnvId();
        assertNotNull(envId);

        // Given - 2. 이미지 2개 등록
        for (int i = 1; i <= 2; i++) {
            EnvImgDto dto = EnvImgDto.builder().envId(envId).build();
            MockMultipartFile mockFile = new MockMultipartFile(
                    "imgFile",
                    "img" + i + ".jpg",
                    "image/jpeg",
                    ("fake image content " + i).getBytes()
            );
            envImgService.register(dto, mockFile);
        }

        // 등록된 이미지 수 검증
        List<EnvImgDto> beforeDeleteList = envImgService.getListByEnvId(envId);
        assertEquals(2, beforeDeleteList.size());

        // When - 게시글 ID로 이미지 전체 삭제
        int deletedCount = envImgService.deleteByEnvId(envId);

        // Then - 삭제된 개수 확인 및 최종 리스트 검증
        assertEquals(2, deletedCount);

        List<EnvImgDto> afterDeleteList = envImgService.getListByEnvId(envId);
        assertTrue(afterDeleteList.isEmpty());
        log.info("게시글 ID로 이미지 전체 삭제 성공. 삭제된 개수: {}", deletedCount);
    }

}
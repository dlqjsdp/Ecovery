//package com.ecovery.service;
//
//import com.ecovery.domain.FreeImgVO;
//import com.ecovery.dto.FreeImgDto;
//import com.ecovery.mapper.FreeImgMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Slf4j
//class FreeImgServiceTest {
//
//    @Autowired
//    private FreeImgService freeImgService;
//
//    @Autowired
//    private FreeImgMapper freeImgMapper;
//
//    @Test
//    @DisplayName("이미지 등록 테스트")
//    @Transactional
//    void testRegisterImage() {
//        // Given
//        FreeImgVO imgVO = FreeImgVO.builder()
//                .freeId(9L) // 존재하는 게시글 번호로 설정
//                .imgName("test123.png")
//                .oriImgName("original.png")
//                .imgUrl("/free/test123.png")
//                .repImgYn("Y")
//                .build();
//
//        // When
//        freeImgService.register(imgVO);
//
//        // Then
//        assertThat(imgVO.getFreeImgId()).isNotNull();
//        log.info("등록된 이미지 ID: {}", imgVO.getFreeImgId());
//    }
//
//    @Test
//    @DisplayName("이미지 전체 조회 테스트")
//    void testGetAllImages() {
//        // Given
//        Long freeId = 7L;
//
//        // When
//        List<FreeImgDto> imageList = freeImgService.getAll(freeId);
//
//        // Then
//        assertThat(imageList).isNotNull();
//        imageList.forEach(img -> log.info("이미지: {}", img));
//    }
//
//    @Test
//    @DisplayName("이미지 단건 조회 테스트")
//    void testGetImage() {
//        // Given
//        Long freeImgId = 6L;
//
//        // When
//        FreeImgDto img  = freeImgService.getById(freeImgId);
//
//        // Then
//        if (img != null) {
//            log.info("조회된 이미지: {}", img );
//            assertThat(img.getFreeImgId()).isEqualTo(freeImgId); // ID가 일치하는지 확인
//        } else {
//            log.warn("해당 ID에 대한 이미지가 존재하지 않습니다.");
//        }
//    }
//
//    @Test
//    @DisplayName("이미지 삭제 테스트")
//    @Transactional
//    void testRemoveImage() {
//        // Given: 이미지 임시 등록
//        FreeImgVO imgVO = FreeImgVO.builder()
//                .freeId(7L)
//                .imgName("delete-test.png")
//                .oriImgName("delete-original.png")
//                .imgUrl("/free/delete-test.png")
//                .repImgYn("N")
//                .build();
//
//        freeImgService.register(imgVO);
//        Long freeImgId = imgVO.getFreeImgId();
//
//        // When
//        boolean result = freeImgService.remove(freeImgId);
//
//        // Then
//        assertThat(result).isTrue();
//        log.info("삭제된 이미지 ID: {}", freeImgId);
//    }
//
//}

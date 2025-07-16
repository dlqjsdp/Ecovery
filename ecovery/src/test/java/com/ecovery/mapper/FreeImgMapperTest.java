package com.ecovery.mapper;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
* 무료나눔 이미지 Mapper 테스트
*  FreeImgMapper의 게시글 CRUD 기능이 정상 동작하는지 단위 테스트 진행
*
 * @author : yeonsu
 * @fileName : FreeImgMapperTest
 * @since : 250715
*/

@SpringBootTest
@Slf4j
class FreeImgMapperTest {

    @Autowired
    private FreeImgMapper freeImgMapper;

    @Test
    @DisplayName("이미지 등록 테스트")
    public void testInsert() {

        // Given(준비) : 새로 등록할 이미지 정보 생성
        FreeImgVO vo = FreeImgVO.builder()
                .freeId(2L)
                .imgName("test2_image")
                .oriImgName("test2.jpg")
                .imgUrl("/images/test2_image.jpg")
                .repImgYn("N")
                .createdAt(LocalDateTime.now())
                .build();

        // When(실행) : 이미지 등록 실행
        freeImgMapper.insert(vo);

        // Then(검증)
        assertNotNull(vo.getFreeId());

        // getFreeImgList로 가져온 데이터 중 방금 등록한 이미지가 있는지 확인
        List<FreeImgDto> list = freeImgMapper.getFreeImgList(2L);
        FreeImgDto dto = list.get(list.size() - 1); // 가장 마지막 이미지가 방금 insert한 이미지..

        assertEquals("test2_image", dto.getImgName());
        assertEquals("test2.jpg", dto.getOriImgName());
        log.info("등록된 이미지 : {}", dto);
    }

    @Test
    @DisplayName("이미지 목록 조회 테스트")
    public void testGetFreeImgList() {

        //Given : 특정 게시글 ID에 이미지가 최소 1개 이상 있다고 가정
        Long freeId = 2L;

        //When : 게시글 ID로 이미지 목록 조회
        List<FreeImgDto> list = freeImgMapper.getFreeImgList(freeId);

        //Then : 결과 검증
        assertNotNull(list);
        assertFalse(list.isEmpty(), "이미지 목록이 비어 있지 않아야합니다.");

        for (FreeImgDto dto : list) {
            log.info("조회된 이미지 : {}", dto);
            assertEquals(freeId, dto.getFreeId());
        }
    }

    @Test
    @DisplayName("이미지 수정 테스트")
    public void testUpdate(){

        //Given : 수정 대상 이미지 조회 (free_id = 2)
        List<FreeImgDto> list = freeImgMapper.getFreeImgList(2L);
        assertFalse(list.isEmpty(), "수정할 이미지가 존재해야합니다.");

        FreeImgDto ori = list.get(list.size() - 1); // 가장 마지막 이미지

        // 수정할 vo 생성
        FreeImgVO vo = FreeImgVO.builder()
                .freeImgId(ori.getFreeImgId())
                .freeId(ori.getFreeImgId())
                .imgName("update_img.jpg")
                .oriImgName("수정!.jpg")
                .imgUrl("/images/update_img.jpg")
                .repImgYn("Y")
                .build();

        // When : update 실행
        int count = freeImgMapper.update(vo);

        // Then: 결과 확인
        if (count == 1) {
            log.info("게시글 수정 성공: {}", vo);
        } else {
            log.info("게시글 수정 실패: {}", vo);
        }
    }

    //대표 이미지 여부에 따라 삭제 처리 테스트
    @Test
    @DisplayName("이미지 삭제 테스트")
    public void testDelete() {

        Long freeId = 2L; // 게시글 ID
        Long targetFreeImgId = 4L; // 삭제하고 싶은 이미지의 ID (예: 사용자가 선택한 이미지)

        // 해당 이미지 정보 조회
        List<FreeImgDto> imageList = freeImgMapper.getFreeImgList(freeId);
        FreeImgDto targetDto = imageList.stream()
                .filter(img -> img.getFreeImgId().equals(targetFreeImgId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));

        log.info("삭제 대상 이미지: {}", targetDto);

        // 대표 이미지 여부 확인
        if ("Y".equals(targetDto.getRepImgYn())) {
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
                throw new IllegalStateException("대표 이미지는 삭제할 수 없습니다.");
            });

            log.info("삭제 실패(대표 이미지): {}", e.getMessage());
            assertEquals("대표 이미지는 삭제할 수 없습니다.", e.getMessage());
        } else {
            int deleted = freeImgMapper.delete(targetFreeImgId);
            assertEquals(1, deleted, "1건 삭제되어야 합니다.");
            log.info("이미지 삭제 성공: {}", targetDto);
        }
    }

    @Test
    @DisplayName("대표 이미지 조회 테스트")
    public void testGetRepImg(){

        // Given : 대표 이미지가 존재하는 게시글 ID
        Long freeId = 7L;

        // When : 대표 이미지 조회 메소드 실행
        FreeImgDto repImg = freeImgMapper.getRepImg(freeId);

        // Then : 결과 검증
        assertNotNull(repImg, "대표 이미지가 존재해야 합니다.");
        assertEquals("Y", repImg.getRepImgYn(), "대표 이미지 플래그는 'Y'여야 합니다.");
        
        log.info("대표 이미지 정보 : {}", repImg);
    }

}





















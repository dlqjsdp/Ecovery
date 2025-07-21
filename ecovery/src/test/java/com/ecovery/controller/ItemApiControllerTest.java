package com.ecovery.controller;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.mapper.ItemMapper;
import com.ecovery.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 상품 Controller Test
 * @author : sehui
 * @fileName : ItemControllerTest
 * @since : 250721
 * @history
 *  - 250721 | sehui | ItemController 모든 메소드 Test 추가
 */

@SpringBootTest
@AutoConfigureMockMvc       //MockMvc 설정 자동 적용
@Transactional
@Rollback(false)    //DB 반영
@Slf4j
class ItemApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("상품 목록 요청")
    public void testList() throws Exception {

        //when : 상품 목록
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/item/list"))
                .andReturn()           //응답 결과 반환
                .getResponse()        //응답 객체
                .getContentAsString();          //응답 본문을 문자열로 반환

        //then: 응답 내용 출력
        log.info("ItemList >>> {}", responseContent);
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ADMIN"})
    @DisplayName("상품 상세 조회 요청")
    public void testItemDtl() throws Exception {

        //given : 상품 ID, 테스트용 이메일 설정
        Long itemId = 10L;

        //when : 상품 상세 조회
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/item/{itemId}", itemId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then : 응답 내용 출력
        log.info("ItemDtl >>> {}", responseContent);
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ADMIN"})
    @DisplayName("상품 등록 페이지")
    public void testItemForm() throws Exception {

        //when : 상품 등록
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/item/new"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //응답 문자열 JSON으로 변환
        ObjectMapper objecMapper = new ObjectMapper();
        JsonNode jsonResponse = objecMapper.readTree(responseContent);
        
        //categories 목록 추출
        JsonNode categoriesNode = jsonResponse.get("categories");

        //then : 응답 내용 출력
        assertNotNull(categoriesNode);
        assertTrue(categoriesNode.isArray());
        assertTrue(categoriesNode.size() > 0);

        log.info("Categories List >>> {}", categoriesNode);
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ADMIN"})
    @DisplayName("상품 등록 요청")
    public void testSaveItem() throws Exception {

        //given : 등록할 상품 정보 생성
        ItemFormDto item = new ItemFormDto();
        item.setItemNm("test 등록2");
        item.setPrice(1000);
        item.setStockNumber(5);
        item.setCategoryId(1L);
        item.setItemDetail("test 상세 설명2");
        item.setItemSellStatus(ItemSellStatus.SELL);

        //DTO -> JSON 변환
        ObjectMapper objecMapper = new ObjectMapper();
        String itemJson = objecMapper.writeValueAsString(item);

        //JSON -> MultipartFile
        MockMultipartFile itemPart = new MockMultipartFile(
                "itemFormDto",
                "itemFormDto.json",
                "application/json",
                itemJson.getBytes(StandardCharsets.UTF_8)
        );

        //MockMultipartFile을 사용해 가짜 이미지 파일 생성
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "itemImgFile",          //@RequestPart 이름과 동일
                "testImage1.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
                "itemImgFile",
                "testImage2.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );

        //when : 상품 등록
        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/item/new")
                .file(itemPart)
                .file(mockFile1)
                .file(mockFile2)
                .with(request -> {
                    request.setMethod("POST");
                    return request;
                })
        )
        .andReturn()
        .getResponse()
        .getContentAsString();

        //then : 결과 출력
        log.info("saveItem >> {}", response);
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ADMIN"})
    @DisplayName("상품 수정 페이지")
    public void testItemModifyPage() throws Exception {

        //given : 수정할 상품 ID
        Long itemId = 10L;

        //when : 상품 정보 조회
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/item/modify/{itemId}", itemId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //the : 결과 검증
        log.info("ItemModify >>> {}", responseContent);

    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ADMIN"})
    @DisplayName("상품 수정 요청")
    public void testItemModify() throws Exception {

        //given : 상품 수정 정보 생성
        Long itemId = 13L;

        ItemFormDto item = new ItemFormDto();
        item.setItemId(13L);
        item.setItemNm("test 수정");
        item.setPrice(60000);
        item.setStockNumber(20);
        item.setCategoryId(2L);
        item.setItemDetail("test 상세 설명_수정");
        item.setItemSellStatus(ItemSellStatus.SELL);

        List<Long> imgId = Arrays.asList(12L);     //실제 DB에 존재하는 ItemImgId (수정하려는 이미지)
        item.setItemImgId(imgId);

        //DTO -> JSON 변환
        ObjectMapper objecMapper = new ObjectMapper();
        String itemJson = objecMapper.writeValueAsString(item);

        //JSON -> MultipartFile
        MockMultipartFile itemPart = new MockMultipartFile(
                "itemFormDto",
                "itemFormDto.json",
                "application/json",
                itemJson.getBytes(StandardCharsets.UTF_8)
        );

        //MockMultipartFile을 사용해 가짜 이미지 파일 생성
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "itemImgFile",          //@RequestPart 이름과 동일
                "testImage222.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );

        //when : 상품 등록
        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/item/modify/{itemId}", itemId)
                        .file(itemPart)
                        .file(mockFile1)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then : 결과 출력
        log.info("updateItem >> {}", response);

    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ADMIN"})
    @DisplayName("상품 삭제")
    public void testItemRemove() throws Exception {

        //given : 삭제할 상품 ID
        Long itemId = 13L;

        //when : 상품 삭제
        mockMvc.perform(MockMvcRequestBuilders.delete("/item/remove/{itemId}", itemId))
                .andReturn()
                .getResponse();

        //then : 결과 검증 (DB에서 확인)
    }

}
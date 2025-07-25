package com.ecovery.controller;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class FreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FreeDto freeDto;

    @BeforeEach
    public void setUp() {
        freeDto = new FreeDto();
        freeDto.setTitle("테스트 제목");
        freeDto.setContent("테스트 내용");
        freeDto.setCategory("가구");
        freeDto.setRegionGu("강남구");
        freeDto.setRegionDong("역삼동");
        freeDto.setItemCondition(ItemCondition.HIGH);
    }


    @Test
    @DisplayName("게시글 목록 조회")
    public void testList() throws Exception {

        // when : 게시글 목록
        String responseContent = mockMvc.perform(get("/api/free/list"))
                .andReturn()// 응답 결과 반환
                .getResponse()// 응답 객체
                .getContentAsString(); // 응답 본문을 문자열로 반환

        // then : 응답 내용 출력
        log.info("FreeList: {}", responseContent);
}

    @Test
    @DisplayName("게시글 상세 조회 요청")
    public void testGet() throws Exception {

        // given : 게시글 ID, 테스트용 이메일 설정
        Long freeId = 1L;

        // when : 게시글 상세 조회
        String responContent = mockMvc.perform(get("/free/{freeId}", freeId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then : 응답 내용 출력
        log.info("FreeGet : {}", responContent);

    }

    @Test
    @DisplayName("게시글 등록 페이지 접근 테스트 - 로그인 사용자")
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    public void testRegisterPage() throws Exception {
        mockMvc.perform(get("/free/register"))
                .andExpect(status().isOk()) // 200 OK 응답을 기대
                .andExpect(view().name("free/register")); // free/register 뷰가 반환되는지 확인
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 게시글 등록 페이지 접근 시 로그인 페이지 이동 테스트")
    public void testRegisterLogin() throws Exception {
        // 인증되지 않은 상태로 /free/register 경로에 GET 요청
        mockMvc.perform(get("/free/register"))
                .andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 응답을 기대
                .andExpect(redirectedUrlPattern("**/member/login")); // 실제 로그인 페이지 URL 패턴에 맞게 조정

    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 게시글 수정 페이지 접근 시 로그인 페이지 이동 테스트")
    public void testModifyPageRedirectToLoginPage() throws Exception {
        // 인증되지 않은 상태로 /free/modify/{freeId} 경로에 GET 요청
        mockMvc.perform(get("/free/modify/1")) // 임의의 freeId 사용
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/member/login"));
    }

    @Test
    @DisplayName("로그인한 사용자가 게시글 수정 페이지 접근 시 성공적으로 페이지가 로드되는지 테스트")
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void modifyPagedUser() throws Exception {
        // 로그인된 상태로 /free/modify/{freeId} 경로에 GET 요청
        mockMvc.perform(get("/free/modify/1")) // 임의의 freeId 사용
                .andExpect(status().isOk())
                .andExpect(view().name("free/modify"));
    }

    @Test
    @DisplayName("게시글 등록 - 인증된 사용자")
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    public void testRegister() throws Exception {
        FreeDto freeDto = FreeDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .category("전자제품")
                .regionGu("강남구")
                .regionDong("역삼동")
                .itemCondition(ItemCondition.HIGH)
                .dealStatus(DealStatus.DONE)
                .imgUrl("tttt.jpg")
                .build();

        String jsonDto = new ObjectMapper().writeValueAsString(freeDto);

        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto", // 반드시 @RequestPart("freeDto")와 이름 동일
                "",
                "application/json",
                jsonDto.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imgFile = new MockMultipartFile(
                "imgFile", "test.png", "image/png", "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/api/free/register")
                        .file(freeDtoPart)
                        .file(imgFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .with(user("test@test.com").roles("USER")))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @WithMockUser(username = "test@test.com", roles = "USER")
    public void testModify() throws Exception {
        // Given
        FreeDto dto = FreeDto.builder()
                .memberId(1L)
                .freeId(7L) // 수정할 게시글 ID
                .title("테스트 제목")
                .content("테스트 내용")
                .category("전자제품")
                .regionGu("강남구")
                .regionDong("역삼동")
                .itemCondition(ItemCondition.HIGH)
                .dealStatus(DealStatus.DONE)
                .imgUrl("tttt.jpg")
                .build();

        // imgDtoList를 null이 아니도록 채워줘야 NPE 안남
        dto.setImgList(List.of(
                FreeImgDto.builder()
                        .freeImgId(5L) // 테스트 전에 등록된 이미지 ID 넣어야 해요
                        .build()
        ));

        // dto -> JSON 직렬화
        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto", "", "application/json",
                new ObjectMapper().writeValueAsBytes(dto)
        );

        // 테스트용 이미지 파일 추가
        MockMultipartFile imgFile = new MockMultipartFile(
                "imgFile", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "dummy image data".getBytes()
        );

        // When & Then
        String response = mockMvc.perform(multipart("/api/free/modify/{freeId}", 7L)
                        .file(freeDtoPart)
                        .file(imgFile)
                        .with(request -> { request.setMethod("PUT"); return request; }) // PUT 방식으로 강제 설정
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("Free Modify Response: {}", response);
    }

//    @Test
//    @DisplayName("게시글 삭제 테스트 - 작성자 or 관리자 권한")
//    @WithMockUser(username = "test@test.com", roles = "USER") // 관리자 테스트 시 roles = {"ADMIN"}
//    public void testDelete() throws Exception {
//        // Given : 삭제 대상 게시글 ID
//        Long freeId = 8L;
//
//        // When & Then : DELETE 방식 multipart 요청 수행 (정상 응답 200 기대)
//        String response = mockMvc.perform(multipart("/api/remove/{freeId}", freeId)
//                        .with(request -> {
//                            request.setMethod("DELETE"); // multipart는 기본 POST이므로 강제로 DELETE로 설정
//                            return request;
//                        })
//                        .with(csrf()) // CSRF 토큰 포함
//                        .contentType(MediaType.MULTIPART_FORM_DATA) // multipart 요청 명시
//                )
//                .andExpect(status().isOk()) // 응답 코드 200 기대
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        log.info("삭제 응답: {}", response);
//    }


}






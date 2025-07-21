package com.ecovery.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/*
 * 환경톡톡 게시글 컨트롤러 통합 테스트
 * 게시글 등록, 수정, 삭제 기능에 대한 컨트롤러 레벨 통합 테스트 수행
 * Spring Security 기반의 관리자 권한(@WithMockUser)을 포함하여
 * MockMvc를 활용한 요청/응답 흐름을 테스트
 * @author : yukyeong
 * @fileName : EnvControllerTest.java
 * @since : 250721
 * @history
 *     - 250721 | yukyeong | 게시글 등록 통합 테스트 메서드(testRegister) 추가
 *     - 250721 | yukyeong | 게시글 수정 통합 테스트 메서드(testModify) 추가
 *     - 250721 | yukyeong | 게시글 삭제 통합 테스트 메서드(testRemove) 추가
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class EnvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("게시글 등록 통합 테스트")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testRegister() throws Exception {

        // Given - 테스트용 게시글 제목/내용 설정
        String title = "통합 테스트 제목";
        String content = "통합 테스트 내용";

        // When - /env/register POST 요청 수행 후 뷰 이름 추출
        String resultPage = mockMvc.perform(MockMvcRequestBuilders
                        .post("/env/register") // POST 요청 (등록 요청)
                        .param("title", title) // form 필드로 title 전송
                        .param("content", content) // form 필드로 content 전송
                        .with(csrf())) // CSRF 토큰 추가 (Spring Security 설정되어 있으면 필수)
                .andReturn() // 요청 실행 후 결과 받기
                .getModelAndView() // ModelAndView 객체 추출
                .getViewName(); // 최종 리턴된 뷰 이름 추출

        // Then (컨트롤러가 글 등록 후 redirect:/env/list로 리다이렉트했는지를 검증)
        assertEquals("redirect:/env/list", resultPage);

        log.info("====> 등록 후 이동한 View 이름:" + resultPage);
    }


    @Test
    @DisplayName("게시글 수정 통합 테스트")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testModify() throws Exception {

        // Given - 먼저 테스트용 게시글을 등록하고 그 ID를 가져옴
        String originalTitle = "수정 전 제목";
        String originalContent = "수정 전 내용";

        // 게시글 등록 요청 수행 (테스트용 데이터 생성)
        mockMvc.perform(MockMvcRequestBuilders.post("/env/register")
                        .param("title", originalTitle)
                        .param("content", originalContent)
                        .with(csrf()))
                .andReturn();

        // 수정할 제목/내용
        String modifiedTitle = "수정 후 제목";
        String modifiedContent = "수정 후 내용";

        // 실제로 등록된 글 중 최신 ID 조회 (서비스나 DB 접근 없이 컨트롤러 테스트만 한다면, DB mocking 필요)
        // 여기선 편의상 1L로 가정. 실제론 등록 후 ID를 DB에서 직접 조회하는 방식 필요
        Long envId = 1L;

        // When - 수정 요청
        String resultPage = mockMvc.perform(MockMvcRequestBuilders
                        .post("/env/modify")
                        .param("envId", String.valueOf(envId)) // 수정 대상 게시글 ID
                        .param("title", modifiedTitle) // 수정된 제목
                        .param("content", modifiedContent) // 수정된 내용
                        .param("pageNum", "1") // 페이징 정보 유지 (Criteria 값들)
                        .param("amount", "10")
                        .param("type", "")
                        .param("keyword", "")
                        .with(csrf()))
                .andReturn()
                .getModelAndView()
                .getViewName();

        // Then - 수정 후 리스트 페이지로 리다이렉트되는지 확인
        assertEquals("redirect:/env/list", resultPage);

        log.info("====> 수정 후 이동한 View 이름: " + resultPage);

    }

    // 수정처리 유효성 검사 실패시 테스트 코드 추가하기



    @Test
    @DisplayName("게시글 삭제 통합 테스트")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testRemove() throws Exception {

        // Given - 먼저 테스트용 게시글을 등록하고 삭제 대상 ID 확보
        String title = "삭제 대상 제목";
        String content = "삭제 대상 내용";

        // 게시글 등록
        mockMvc.perform(MockMvcRequestBuilders.post("/env/register")
                        .param("title", title)
                        .param("content", content)
                        .with(csrf()))
                .andReturn();

        // 테스트에서는 간단히 1번 ID로 가정 (실제 프로젝트에선 DB에서 ID 추출 필요)
        Long envId = 1L;

        // When - 삭제 요청 (Criteria 값 포함)
        String resultPage = mockMvc.perform(MockMvcRequestBuilders.post("/env/remove")
                        .param("envId", String.valueOf(envId)) // String 타입만 허용하기 때문
                        .param("pageNum", "1")
                        .param("amount", "10")
                        .param("type", "")
                        .param("keyword", "")
                        .with(csrf()))
                .andReturn()
                .getModelAndView()
                .getViewName();

        // Then - 삭제 후 목록 페이지로 리다이렉트되는지 확인
        assertEquals("redirect:/env/list", resultPage);

        log.info("====> 삭제 후 이동한 View 이름: " + resultPage);
    }

}
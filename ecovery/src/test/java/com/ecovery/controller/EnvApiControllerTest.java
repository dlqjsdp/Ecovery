package com.ecovery.controller;

import com.ecovery.dto.EnvDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * EnvApiController에 대한 통합 테스트 클래스
 * REST API 컨트롤러의 주요 기능(목록 조회, 등록, 수정, 삭제)에 대해 통합 테스트 수행
 * MockMvc를 사용하여 HTTP 요청/응답 시나리오를 시뮬레이션
 * @author : yukyeong
 * @fileName : EnvApiControllerTest.java
 * @since : 250722
 * @history
     - 250722 | yukyeong | 게시글 목록 조회 API 테스트 (검색 조건 포함/없음) 구현
     - 250722 | yukyeong | 게시글 등록 API 테스트 (정상 등록 케이스) 구현
     - 250722 | yukyeong | 게시글 수정 API 테스트 (정상 수정 케이스) 구현
     - 250722 | yukyeong | 게시글 삭제 API 테스트 (정상 삭제 케이스) 구현
     - 예정   | yukyeong | 게시글 등록/수정 유효성 실패 및 예외 처리 테스트 추가 예정
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class EnvApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON 응답 파싱용

    @Test
    @DisplayName("환경톡톡 게시글 목록 조회 API 테스트")
    public void testGetEnvList() throws Exception {

        // when: GET 요청 수행 (검색어: "게시글", 제목 검색 조건, 1페이지, 10개씩)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/env/list")
                        .param("pageNum", "1")
                        .param("amount", "10")
                        .param("type", "T") // 제목 검색
                        .param("keyword", "게시글") // 검색어
                )
                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK
                .andExpect(jsonPath("$.list").isArray()) // list가 배열인지 확인
                .andExpect(jsonPath("$.pageMaker").exists()) // pageMaker가 존재하는지 확인
                .andExpect(jsonPath("$.type").value("T")) // type 값 검증 (검색 타입)
                .andExpect(jsonPath("$.keyword").value("게시글")) // keyword 값 검증 (검색어)
                .andReturn();

        // then: 결과 응답 JSON 출력
        String responseBody = result.getResponse().getContentAsString();
        log.info("응답 JSON -----> {}", responseBody);
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 API 테스트(검색어 없이)")
    public void testGetEnvListWithoutSearch() throws Exception {
        // when: 검색 조건 없이 전체 목록 요청 (1페이지, 10개씩)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/env/list")
                        .param("pageNum", "1")
                        .param("amount", "10")
                )
                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK
                .andExpect(jsonPath("$.list").isArray()) // list가 배열인지 확인
                .andExpect(jsonPath("$.pageMaker").exists()) // pageMaker가 존재하는지 확인
                .andReturn();

        // then: 응답 내용 출력
        String responseBody = result.getResponse().getContentAsString();
        log.info("응답 JSON -----> {}", responseBody);
    }

    @Test
    @DisplayName("API 컨트롤러 - 게시글 등록 성공 테스트")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testRegister() throws Exception {
        // Given - 게시글 제목/내용
        EnvDto dto = new EnvDto(); // 게시글 DTO 객체 생성
        dto.setTitle("API 통합 테스트 제목"); // 제목 설정
        dto.setContent("API 통합 테스트 내용"); // 내용 설정

        // ObjectMapper를 사용해 DTO 객체를 JSON 문자열로 변환
        // 실제 컨트롤러가 @RequestBody로 JSON을 받을 것이기 때문에 이 형식으로 요청을 보냄
        String json = objectMapper.writeValueAsString(dto);

        // When - mockMvc를 사용하여 컨트롤러에 POST 요청 수행
        MvcResult result = mockMvc.perform(post("/api/env/register") // 요청 경로: POST /api/env/register
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문의 타입을 application/json으로 지정
                        .content(json) // 변환된 JSON 데이터를 요청 본문에 포함
                        .with(csrf())) // CSRF 토큰 추가 (Spring Security에서 필수)
                .andExpect(MockMvcResultMatchers.status().isCreated()) // 기대하는 응답 상태: 201 Created
                .andReturn(); // 결과(MvcResult) 반환

        // Then - 응답 결과(JSON 문자열)를 출력해서 실제 반환 값을 확인
        String response = result.getResponse().getContentAsString(); // 응답 본문을 문자열로 추출
        log.info("====> 응답 본문: " + response);  // 응답 내용 로그 출력 (예: {"envId":63})

    }

    // 게시글 등록 유효성 실패 테스트, 예외 처리 테스트 추가하기

    @Test
    @DisplayName("API 컨트롤러 - 게시글 수정 성공 테스트")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testModify() throws Exception {
        // Given - 수정할 게시글 데이터 (게시글 ID가 DB에 실제로 존재해야 함)
        EnvDto dto = new EnvDto();
        dto.setEnvId(11L); // 기존 게시글 ID
        dto.setTitle("수정된 제목입니다"); // 수정할 제목
        dto.setContent("수정된 내용입니다"); // 수정할 내용

        // EnvDto 객체를 JSON 문자열로 변환 (HTTP 요청 본문에 담기 위해)
        String json = objectMapper.writeValueAsString(dto);

        // When & Then - 수정 API 호출 및 응답 검증
        mockMvc.perform(put("/api/env/modify/11") // 요청 URI (PUT 방식)
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문 타입 설정
                        .content(json) // JSON 데이터 전송
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isOk()) // 응답 상태 코드가 200 OK인지 검증
                .andExpect(jsonPath("$.message").value("수정 성공")) // 응답 JSON의 message 값 검증
                .andExpect(jsonPath("$.envId").value(11L)); // 응답 JSON의 envId 값 검증
    }

    // 게시글 수정 유효성 실패 테스트, 예외 처리 테스트 추가하기


    @Test
    @DisplayName("API 컨트롤러 - 게시글 삭제 성공 테스트")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testRemove() throws Exception {
        // Given - 삭제할 게시글 ID (DB에 존재해야 함)
        Long envId = 11L;

        // When & Then - 삭제 API 호출 및 응답 검증
        mockMvc.perform(delete("/api/env/remove/{envId}", envId) // DELETE 요청
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.message").value("삭제되었습니다.")) // 메시지 검증
                .andExpect(jsonPath("$.envId").value(envId)); // 삭제된 ID 검증
    }
}
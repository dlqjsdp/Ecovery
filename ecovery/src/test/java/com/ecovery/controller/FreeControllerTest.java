//package com.ecovery.controller;
//
//import com.ecovery.constant.ItemCondition;
//import com.ecovery.domain.FreeVO;
//import com.ecovery.service.FreeService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.Principal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@Slf4j
//public class FreeControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
////    @Test
////    @DisplayName("게시글 목록 조회")
////    @
////    void testList() throws Exception {
////        mockMvc.perform(get("/free/list?pageNum=1&amount=10"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.list").exists())
////                .andExpect(jsonPath("$.pageMaker").exists());
////    }
//
//    @Test
//    @DisplayName("게시글 등록 테스트")
//    @WithMockUser(username = "test@test.com", roles = {"USER"})
//    public void testRegister() throws Exception {
//
//        //Given - 테스트용 게시글 제목/내용 설정
//        String title = "게시글 등록 테스트 제목";
//        String content = "게시글 등록 테스트 내용";
//
//        //When - /free/register POST 요청 수행 후 뷰 이름 추출
//        String resultPage = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/free/register") // POST 요청 (등록 요청)
//                        .param("title", title) // form 필드로 title 전송
//                        .param("content", content) // form 필드로 content 전송
//                        .with(csrf())) // csrf 토큰 추가 (Spring Security 설정되어 있으면 필수)
//                        .andReturn() // 요청 실행 후 결과 받기
//                        .getModelAndView() // ModelAndView 객체 추출
//                        .getViewName(); // 최종 리턴된 뷰 이름 추출
//
//        //Then (컨트롤러가 글 등록 후 redirect:/free/list로 리다이렉트했는지를 검증)
//        assertEquals("redirect:/free/list", resultPage);
//        log.info("등록 후 이동한 View 이름: " + resultPage);
//    }
//
//
//}

package com.ecovery.controller;

import com.ecovery.constant.ItemCondition;
import com.ecovery.domain.FreeVO;
import com.ecovery.service.FreeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환용

    private String testEmail = "test@example.com"; // Principal용 테스트 사용자 이메일

    private Principal mockPrincipal = () -> testEmail;

    @Test
    @DisplayName("게시글 목록 조회")
    void testList() throws Exception {
        mockMvc.perform(get("/free/list?pageNum=1&amount=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").exists())
                .andExpect(jsonPath("$.pageMaker").exists());
    }

    @Test
    @DisplayName("게시글 등록")
    void testRegister() throws Exception {
        FreeVO vo = new FreeVO();
        vo.setMemberId(1L);
        vo.setTitle("테스트 제목");
        vo.setContent("테스트 내용");
        vo.setCategory("가구");
        vo.setRegionGu("강남구");
        vo.setRegionDong("역삼동");
        vo.setItemCondition(ItemCondition.HIGH);

        String json = objectMapper.writeValueAsString(vo);

        mockMvc.perform(post("/free/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .principal(mockPrincipal)) // 로그인 사용자 흉내
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 등록되었습니다."));
    }

    @Test
    @DisplayName("게시글 상세 조회")
    void testGet() throws Exception {
        Long targetId = 1L;

        mockMvc.perform(get("/free/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.free").exists())
                .andExpect(jsonPath("$.img").exists());
    }

    @Test
    @DisplayName("게시글 수정")
    void testModify() throws Exception {
        FreeVO vo = new FreeVO();
        vo.setTitle("수정된 제목");
        vo.setContent("수정된 내용");
        vo.setCategory("가전");
        vo.setRegionGu("서초구");
        vo.setRegionDong("서초동");
        vo.setItemCondition(ItemCondition.HIGH);

        String json = objectMapper.writeValueAsString(vo);

        mockMvc.perform(put("/free/modify/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .principal(mockPrincipal)) // 로그인 사용자 흉내
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 수정되었습니다."));
    }

    @Test
    @DisplayName("게시글 삭제")
    void testRemove() throws Exception {
        mockMvc.perform(delete("/free/remove/1")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 삭제되었습니다."));
    }
}

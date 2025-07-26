package com.ecovery.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 환경톡톡 이미지 DTO
 * 클라이언트 <-> 서비스 계층 간 데이터 전송용 객체
 * API 응답 또는 뷰 렌더링용으로 사용됨
 *
 * @author : yukyeong
 * @fileName : EnvImgDto
 * @since : 250726
 * @history
 *     - 250726 | yukyeong | 환경톡톡 이미지 DTO 클래스 최초 생성
 */

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnvImgDto {

    private Long envImgId; // 이미지 ID (PK)
    private Long envId; // 게시글 ID (FK)
    private String imgName; // 서버 저장 이미지 파일명
    private String oriImgName; // 원본 파일명
    private String imgUrl; // 이미지 접근 URL
    private LocalDateTime createdAt; // 등록일
}

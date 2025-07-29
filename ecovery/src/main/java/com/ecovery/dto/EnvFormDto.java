package com.ecovery.dto;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 환경톡톡 게시글 등록/수정용 Form DTO
 * - 게시글 정보(EnvDto)와 이미지 ID 목록을 함께 전달받기 위한 용도
 * - REST API에서 @RequestPart로 multipart/form-data를 처리할 때 사용됨
 *
 * @author : yukyeong
 * @fileName : EnvFormDto
 * @since : 250728
 * @history
     - 250728 | yukyeong | 게시글 등록 및 수정용 EnvFormDto 생성 (EnvDto + 이미지 리스트)
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvFormDto {

    @Valid
    private EnvDto envDto; // 게시글 본문 정보

    private List<Long> deleteImgIds = new ArrayList<>(); // 이미지 수정. 삭제용 ID 리스트

    private List<MultipartFile> envImgFiles = new ArrayList<>(); // 이미지 파일들 추가

    public EnvFormDto(EnvDto envDto) {
        this.envDto = envDto;
        this.envImgFiles = new ArrayList<>(); // null 방지 초기화
    }
}

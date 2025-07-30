package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvFormDto;
import com.ecovery.dto.PageDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.EnvService;
import com.ecovery.service.FileService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 환경톡톡 게시글 컨트롤러
 * 게시글 등록, 조회, 수정, 삭제, 목록 조회(페이징) 기능의 컨트롤러 역할을 수행
 * 클라이언트의 요청을 받아 서비스 계층(EnvService)과 상호작용하여 처리하고 View에 데이터를 전달
 * @author : yukyeong
 * @fileName : EnvApiController
 * @since : 250722
 * @history
   - 250722 | yukyeong | REST API 기반 게시글 목록 조회(list), 단건 조회(get/{envId}) 구현
   - 250722 | yukyeong | 게시글 등록 API (POST /register) 구현 - 유효성 검사 및 사용자 인증 포함
   - 250722 | yukyeong | 게시글 수정 API (PUT /modify/{envId}) 구현 - 유효성 검사 및 수정 처리
   - 250722 | yukyeong | 게시글 삭제 API (DELETE /remove/{envId}) 구현
   - 250723 | yukyeong | 게시글 단건 조회 예외처리 추가 (404 반환)
   - 250724 | yukyeong | 게시글 삭제 실패 시 400 Bad Request 응답 처리
   - 250728 | yukyeong | 게시글 등록/수정 multipart/form-data 방식으로 전환 (EnvFormDto + 이미지 포함)
                         게시글 수정 시 BindingResult 제거 후 수동 유효성 검사 도입
                         게시글 삭제 시 컨트롤러는 그대로 유지 (서비스에서 이미지 함께 삭제 처리)
   - 250730 | yukyeong | 이미지 임시 업로드 API (POST /upload-temp) 구현 - Multipart 이미지 리스트 받아 UUID로 저장 후 파일명 반환
   - 250730 | yukyeong | 이메일 null 방지를 위한 사용자 인증 정보 처리 보강
                           - Authentication → CustomUserDetails로 이메일 안전하게 추출
                           - 회원 정보 없을 경우 401 UNAUTHORIZED 반환
                           - 등록 전후 EnvDto 상태 로깅(log.info) 추가
   - 250730 | yukyeong | 이미지 임시 업로드 API (POST /upload-temp) 구현 - Multipart 이미지 리스트 받아 UUID로 저장 후 파일명 반환
 */

@RestController
@RequestMapping("/api/env")
@RequiredArgsConstructor
@Slf4j
public class EnvApiController {

    private final EnvService envService;
    private final MemberService memberService;

    private final FileService fileService;

    @Value("${envImgLocation}")
    private String envImgLocation;

    // 목록 조회
    // @RequestParam(required = false)은 검색 조건이 없어도 요청 가능하게 함
    //Criteria cri는 페이징 및 검색 조건 객체
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String type,
                                                    @RequestParam(required = false) String keyword,
                                                    Criteria cri) {
        log.info("환경톡톡 게시글 목록 API 호출");

        // 게시글 목록 조회 (페이징 및 검색 조건 포함)
        List<EnvDto> list = envService.getList(cri);
        // 전체 게시글 수 조회 (페이징 계산용)
        int total = envService.getTotal(cri);

        // 응답 데이터 구성
        // 응답은 list, pageMaker, type, keyword를 포함한 JSON 형태
        Map<String, Object> response = new HashMap<>();
        response.put("list", list); // 게시글 목록
        response.put("pageMaker", new PageDto(cri, total)); // 페이지 정보
        response.put("type", type); // 검색 타입
        response.put("keyword", keyword); // 검색어

        // HTTP 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }


    @PostMapping("/upload-temp")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> uploadTempImages(@RequestPart List<MultipartFile> imageFiles) {
        try {
            List<String> fileNames = new ArrayList<>();

            for (MultipartFile file : imageFiles) {
                String originalName = file.getOriginalFilename();
                if (originalName != null && !originalName.isBlank()) {
                    // UUID로 파일 저장
                    String savedName = fileService.uploadFile(envImgLocation, originalName, file.getBytes());
                    fileNames.add(savedName); // 저장된 파일명(UUID 확장자)
                }
            }

            // 실제 저장된 파일명을 리스트로 반환
            return ResponseEntity.ok(fileNames);

        } catch (Exception e) {
            log.error("임시 이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }

    // 게시글 등록 처리
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestPart("envFormDto") @Valid EnvFormDto envFormDto,
            BindingResult bindingResult,
            @RequestPart(value = "envImgFiles", required = false) List<MultipartFile> envImgFiles,
            Authentication auth) {

        // 1. @Valid 유효성 검사 + BindingResult로 검증
        // EnvDto에 설정한 유효성 검증에 실패하면 400 Bad Request 응답 반환
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력값을 다시 확인해주세요.");
        }

        // 2. 유효성 검사 통과한 경우에만 로그인 사용자 이메일 → memberId 조회
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail(); // Principal에서 로그인한 사용자의 이메일을 가져옴 (username 역할)

        MemberVO member = memberService.getMemberByEmail(email);

        if (member == null) {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 만료되었거나, 사용자 정보를 찾을 수 없습니다.");
        }

        Long memberId = member.getMemberId();

        envFormDto.getEnvDto().setMemberId(memberId); // 조회한 memberId를 등록할 게시글 DTO에 설정

        if (envImgFiles == null || envImgFiles.isEmpty()) {
            log.info("이미지 파일이 없습니다. 이미지 없이 등록을 진행합니다.");
        } else {
            log.info("업로드된 이미지 수: {}", envImgFiles.size());
        }

        log.info("게시글 등록 처리 전: {}", envFormDto); // 등록 전 EnvDto 상태 출력 (등록 전에 memberId가 잘 들어갔는지 확인용)

        // 3. 게시글 등록 처리
        try {
            envService.register(envFormDto, envImgFiles); // DB에 INSERT 수행

            log.info("게시글 등록 처리 후: {}", envFormDto); // 등록 후 EnvDto 상태 출력 (envId 등 자동 생성된 필드 확인 가능)

            // 응답 데이터 구성: 등록된 게시글의 ID(envId)를 JSON으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("envId", envFormDto.getEnvDto().getEnvId()); // INSERT 후 Mapper에서 envId가 세팅되었다고 가정
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 상태 코드 201(CREATED)와 함께 응답 본문에 envId 포함
        } catch (Exception e) {
            // 등록 중 예외 발생 시 로그 출력 후 500 INTERNAL_SERVER_ERROR 반환
            log.error("게시글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류가 발생했습니다."); // 서버 에러 시 500
        }
    }


    // 게시글 단건 조회, 수정 버튼 노출 조건 처리
    @GetMapping("/get/{envId}")
    public ResponseEntity<?> get(@PathVariable Long envId) {
        log.info("게시글 단건 조회 API 호출 - envId: {}", envId);

        try {
            // 1. 조회수 증가
            envService.increaseViewCount(envId);

            // 2. 게시글 단건 조회
            EnvDto envDto = envService.get(envId);

            if (envDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("해당 게시글을 찾을 수 없습니다.");
            }

            // 3. 결과 반환
            return ResponseEntity.ok(envDto); // 상태코드 200 OK + 게시글 정보 반환

        } catch (Exception e) {
            log.error("게시글 단건 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 조회 중 오류가 발생했습니다.");
        }
    }


    // 게시글 수정 처리
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/modify/{envId}")
    public ResponseEntity<?> modify(
            @PathVariable Long envId,  // URL 경로에서 수정할 게시글의 ID 추출
            @Valid @RequestPart("envFormDto") EnvFormDto envFormDto,  // multipart/form-data 요청에서 게시글 정보(JSON 형태) 추출
            @RequestPart(value = "envImgFiles", required = false) List<MultipartFile> envImgFiles) {  // 첨부 이미지 파일 리스트

        log.info("게시글 수정 요청 (API): envId = {}, formDto = {}", envId, envFormDto);

        try {
            // 1. EnvFormDto에서 게시글 본문 DTO(EnvDto) 추출
            EnvDto envDto = envFormDto.getEnvDto();
            // 2. 수정 대상 게시글 ID 설정 (경로에서 받은 값으로 세팅)
            envDto.setEnvId(envId);

            // 3. 유효성 검증 수동 처리 (클라이언트에서 필수값이 누락되었는지 확인)
            if (envDto.getTitle() == null || envDto.getTitle().isBlank()) {
                return ResponseEntity.badRequest().body("제목은 필수입니다.");
            }
            if (envDto.getContent() == null || envDto.getContent().isBlank()) {
                return ResponseEntity.badRequest().body("내용은 필수입니다.");
            }

            // 4. 이미지 파일이 전달되지 않았을 경우 빈 리스트로 초기화 (null 방지)
            if (envImgFiles == null) {
                envImgFiles = List.of();
            }

            // 5. 서비스 계층에 수정 요청 (게시글 + 이미지 수정 처리)
            boolean success = envService.modify(envFormDto, envImgFiles);

            // 6. 수정 성공 여부에 따라 응답 처리
            if (success) {
                // 성공 시 메시지와 수정된 게시글 ID 포함하여 200 OK 응답
                Map<String, Object> result = new HashMap<>();
                result.put("message", "수정 성공");
                result.put("envId", envId);
                return ResponseEntity.ok(result);
            } else {
                // 수정 실패 시 400 Bad Request 응답
                return ResponseEntity.badRequest().body("수정에 실패했습니다.");
            }

        } catch (Exception e) {
            // 예외 발생 시 서버 내부 오류로 처리 (500)
            log.error("게시글 수정 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }


    // 게시글 삭제 처리
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/remove/{envId}")
    public ResponseEntity<?> remove(@PathVariable Long envId) {

        log.info("게시글 삭제 요청 (API): {}", envId);

        try {
            boolean success = envService.remove(envId); // 게시글 삭제 시도

            if (success) {
                // 삭제 성공 시 메시지 포함해서 200 OK 응답
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제되었습니다.");
                response.put("envId", envId);
                return ResponseEntity.ok(response);
            } else {
                String errorMessage = "삭제에 실패했습니다.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

        } catch (Exception e) {
            String errorMessage = "삭제 중 에러가 발생하였습니다.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
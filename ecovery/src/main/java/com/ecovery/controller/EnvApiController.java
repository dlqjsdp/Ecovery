package com.ecovery.controller;

import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.EnvService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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
   - 25-723 | yukyeong | 게시글 단건 조회 예외처리 추가 (404 반환)
 */

@RestController
@RequestMapping("/api/env")
@RequiredArgsConstructor
@Slf4j
public class EnvApiController {

    private final EnvService envService;
    private final MemberService memberService;

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


    // 게시글 등록 처리
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody EnvDto envDto,
                           BindingResult bindingResult,
                           Principal principal) {

        // 1. @Valid 유효성 검사 + BindingResult로 검증
        // EnvDto에 설정한 유효성 검증에 실패하면 400 Bad Request 응답 반환
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // 2. 유효성 검사 통과한 경우에만 로그인 사용자 이메일 → memberId 조회
        String email = principal.getName(); // Principal에서 로그인한 사용자의 이메일을 가져옴 (username 역할)
        Long memberId = memberService.getMemberByEmail(email).getMemberId(); // 이메일을 기준으로 DB에서 memberId 조회
        envDto.setMemberId(memberId); // 조회한 memberId를 등록할 게시글 DTO에 설정

        log.info("게시글 등록 처리 전: {}", envDto); // 등록 전 EnvDto 상태 출력 (등록 전에 memberId가 잘 들어갔는지 확인용)

        // 3. 게시글 등록 처리
        try {
            envService.register(envDto); // DB에 INSERT 수행

            log.info("게시글 등록 처리 후: {}", envDto); // 등록 후 EnvDto 상태 출력 (envId 등 자동 생성된 필드 확인 가능)

            // 응답 데이터 구성: 등록된 게시글의 ID(envId)를 JSON으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("envId", envDto.getEnvId()); // INSERT 후 Mapper에서 envId가 세팅되었다고 가정
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 상태 코드 201(CREATED)와 함께 응답 본문에 envId 포함
        } catch (Exception e) {
            // 등록 중 예외 발생 시 로그 출력 후 500 INTERNAL_SERVER_ERROR 반환
            log.error("게시글 등록 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 서버 에러 시 500
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
    public ResponseEntity<?> modify(@PathVariable Long envId,
                                    @Valid @RequestBody EnvDto envDto,
                            BindingResult bindingResult) {

        log.info("게시글 수정 요청 (API): {}", envDto);

        // 1. 유효성 검사 실패 시 에러 응답 반환
        if (bindingResult.hasErrors()) {
            // @Valid 검증에 실패한 경우 400 Bad Request 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("입력값을 다시 확인해주세요.");
        }

        envDto.setEnvId(envId); // 경로에서 받은 ID를 DTO에 주입

        // 2. 수정 로직 처리
        try {
            boolean success = envService.modify(envDto); // 서비스 계층에서 게시글 수정 수행
            if (success) {
                // 수정 성공 시 200 OK와 함께 응답 데이터 반환
                Map<String, Object> response = new HashMap<>();
                response.put("message", "수정 성공");
                response.put("envId", envDto.getEnvId()); // 수정된 게시글 ID도 같이 반환
                return ResponseEntity.ok(response);
            } else {
                // 수정 실패 시 400 Bad Request 상태 코드와 실패 메시지 반환
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("수정에 실패했습니다.");
            }
        } catch (Exception e) {
            // 예외 발생 시 500 Internal Server Error와 함께 오류 메시지 반환
            log.error("게시글 수정 중 오류 발생", e);
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }

        } catch (Exception e) {
            String errorMessage = "삭제 중 에러가 발생하였습니다.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
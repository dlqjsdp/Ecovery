package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.dto.PageDto;
import com.ecovery.mapper.FreeMapper;
import com.ecovery.service.FreeImgService;
import com.ecovery.service.FreeService;
import com.ecovery.service.MemberService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 무료나눔 게시글 컨트롤러
 * 게시글 등록, 조회, 수정, 삭제 등 CRUD 기능을 제공
 * 회원 인증 및 권한(Role)을 기반으로 접근 권한을 제어
 *
 * [로그 기준] 중요한 요청 흐름, 권한 체크, 실패 가능성이 있는 작업에만 로그 작성
 *
 * [기능별 접근 제어]
 * - 목록 조회, 상세 조회: 로그인 여부 관계없이 모두 가능
 * - 등록: 로그인한 사용자만 가능
 * - 수정: 작성자 본인만 가능
 * - 삭제: 작성자 본인 또는 관리자만 가능
 *
 * @author : yeonsu
 * @fileName : FreeRestController
 * @since : 250721
 * @edit : 250724 - 수동 유효성 검사 코드 추가
 */

@RestController
@RequestMapping("/api/free")
@RequiredArgsConstructor
@Slf4j
public class FreeApiController {

    private final FreeService freeService;
    private final MemberService memberService;
    private final Validator validator; // 수동 유효성 검사 - JSON + 이미지 같이 받을땐 @Valid 작동 안해서

    /*
      게시글 등록 요청 처리
     - 로그인한 사용자(회원 또는 관리자)만 등록 가능
     - 게시글 정보(freeDto)와 이미지(imgFiles)를 multipart/form-data 형식으로 전달받음
     - 유효성 검사, 사용자 인증, 서비스 로직 호출 후 결과 반환
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> register(
            @RequestPart("freeDto") FreeDto freeDto, // 게시글 정보(JSON 형식)
            @RequestPart("imgFile") List<MultipartFile> imgFiles, // 이미지 파일 목록
            Principal principal) {                              // 로그인한 사용자 정보

        // 로그인 체크
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 수동 유효성 검사
        // @Valid가 작동하지 않아서 Validator를 사용해 직접 DTO의 유효성 검사 수행
        Set<ConstraintViolation<FreeDto>> violations = validator.validate(freeDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<FreeDto> v : violations) {
                sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("\n");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효성 오류:\n" + sb);
        }

        // 회원 이메일 -> DB에서 memberId 조회 후 DTO에 설정
        String email = principal.getName();
        Long memberId = memberService.getMemberByEmail(email).getMemberId();
        freeDto.setMemberId(memberId);

        // 게시글 등록 시도 (서비스 호출)
        try {
            freeService.register(freeDto, imgFiles); // 게시글 + 이미지 등록
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 등록되었습니다.");
        } catch (Exception e) {
            log.error("게시글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류가 발생했습니다.");
        }
    }


    /*
        게시글 목록 조회 API
        - 페이징 정보(Criteria)를 기반으로 전체 게시글 목록과 페이징 정보 반환
        - 누구나 접근 가능 (로그인 불필요)
    */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(Criteria cri){
        // 요청된 페이지 정보 로그 출력 (예 : pageNum-1, amount = 10)
        log.info("게시글 목록 조회 - pageNum: {}, amount: {}", cri.getPageNum(), cri.getAmount());

        // 페이징 조건에 따라 게시글 목록 조회
        List<FreeDto> list = freeService.getAll(cri);
        log.info("조회된 게시글 수 : {}", list.size());

        // 전체 게시글 수 조회 (페이징 계산용)
        int total = freeService.getTotalCount(cri);

        // 결과 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("list", list); // 게시글 리스트
        result.put("pageMaker", new PageDto(cri, total)); //페이지네이션 정보

        // JSON 형식으로 200 OK 응답 반환
        return ResponseEntity.ok(result);
    }


    /*
        게시글 상세 조회 API
     - 특정 게시글 ID를 통해 상세 정보를 조회
     - 이미지 정보(imgList)까지 포함된 FreeDto 반환
     - 누구나 접근 가능 (로그인 불필요)
    */
    @GetMapping("/{freeId}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long freeId){
        log.info("게시글 상세 조회 요청 - freeId: {}", freeId);

        // 게시글 상세 정보 조회 (이미지 리스트 포함된 DTO)
        FreeDto free = freeService.get(freeId);

        // 응답 데이터를 Map 형태로 구성(key:"free")
        Map<String, Object> result = new HashMap<>();
        result.put("free", free); // free.imgList 도 포함됨

        return ResponseEntity.ok(result);
    }


    /*
     게시글 수정 API
    - 작성자 또는 관리자가 게시글 수정 가능
    - 이미지 포함된 수정 데이터 처리
    - 유효성 검사, 권한 확인, 오류 처리 포함
    */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/modify/{freeId}")
    public ResponseEntity<String> modify(
            @PathVariable Long freeId,
            @RequestPart("freeDto") FreeDto freeDto,
            @RequestPart("imgFile") List<MultipartFile> imgFiles,
            Principal principal) {

        // 로그인 여부 확인
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 현재 로그인한 사용자 정보 조회
        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        // 권한 확인 (작성자 또는 관리자)
        Role role = memberService.getMemberByEmail(email).getRole();

        // 수정 대상 게시글의 작성자 정보 조회
        FreeDto origin = freeService.get(freeId);

        // 권한 확인 (작성자 또는 관리자만 수정 가능)
        if (!origin.getMemberId().equals(currentUserId) && role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        // 수정 요청 DTO에 게시글 Id 및 로그인 사용자 Id 설정
        freeDto.setFreeId(freeId);
        freeDto.setMemberId(currentUserId);

        // 수동 유효성 검사 수행 (JSON + 이미지 multipart 구조이기 때문에 직접 검증)
        Set<ConstraintViolation<FreeDto>> violations = validator.validate(freeDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<FreeDto> v : violations) {
                sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("\n");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효성 오류:\n" + sb);
        }

        // 수정 처리 시도 후 결과에 따라 응답 반환
        try {
            boolean result = freeService.modify(freeDto, imgFiles);
            return result
                    ? ResponseEntity.ok("게시글이 수정되었습니다.")
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 중 오류가 발생했습니다.");
        }
    }


    /*
       게시글 삭제 API
    - 게시글의 작성자 또는 관리자가 삭제 가능
    - 로그인/권한 확인, 게시글 정보 조회, 삭제 로직 수행
    */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/remove/{freeId}")
    public ResponseEntity<String> remove(@PathVariable Long freeId, Principal principal) {

        // 로그인 여부 확인
        if (principal == null) {
            log.info("비회원이 게시글 삭제 시도 - freeId: {}", freeId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 사용자의 이메일로 회원정보 조회후, 권한체크(ADMIN인지, USER인지), 현재 로그인한 사용자 ID 조회
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();

        // 삭제 대상 게시글의 정보를 DB에서 조회 후 해당 게시글 작성자 가져오기
        FreeDto dto = freeService.get(freeId);
        Long postOwnerId = dto.getMemberId();

        // 삭제 권한 체크 : 작성자도 아니고, 관리자도 아닌 경우 거부
        if (!currentUserId.equals(postOwnerId) && role != Role.ADMIN) {
            log.info("삭제 권한 없음 - 요청자 ID: {}, 작성자 ID: {}, role: {}", currentUserId, postOwnerId, role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        // 게시글 삭제 조건 맞추기 위해 dto에 작성자 ID 세팅 (서비스에서 사용)
        dto.setMemberId(postOwnerId); // memberId 넣어줘야 delete 조건에 맞음

        // 실제 삭제 로직 호출
        boolean result = freeService.remove(dto);

        // 삭제 성공/실패에 따른 응답 반환
        return result
                ? ResponseEntity.ok("게시글이 삭제되었습니다.") // 200 OK
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패"); // 500 에러
    }
}
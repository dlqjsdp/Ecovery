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
import jakarta.validation.Valid;
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
 */

@RestController
@RequestMapping("/api/free")
@RequiredArgsConstructor
@Slf4j
public class FreeApiController {

    private final FreeService freeService;
    private final MemberService memberService;
    private final FreeMapper freeMapper;

    // 게시글 등록 - 사용자, 관리자 가능
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestPart("freeDto") FreeDto freeDto,
            BindingResult bindingResult,
            @RequestPart("imgFile") List<MultipartFile> imgFiles,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력값을 확인해주세요.");
        }

        String email = principal.getName();
        Long memberId = memberService.getMemberByEmail(email).getMemberId();
        freeDto.setMemberId(memberId);

        try {
            freeService.register(freeDto, imgFiles);
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 등록되었습니다.");
        } catch (Exception e) {
            log.error("게시글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류가 발생했습니다.");
        }
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(Criteria cri){
        log.info("게시글 목록 조회 - pageNum: {}, amount: {}", cri.getPageNum(), cri.getAmount());

        List<FreeDto> list = freeService.getAll(cri);
        int total = freeService.getTotalCount(cri);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("pageMaker", new PageDto(cri, total));

        return ResponseEntity.ok(result);
    }

    // 게시글 상세 조회
    @GetMapping("/{freeId}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long freeId){
        log.info("게시글 상세 조회 요청 - freeId: {}", freeId);

        FreeDto free = freeService.get(freeId);  // 이미지까지 포함된 DTO

        Map<String, Object> result = new HashMap<>();
        result.put("free", free); // free.imgList 도 포함됨

        return ResponseEntity.ok(result);
    }

    // 게시글 수정 - 작성자, 관리자 둘 다 가능
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/modify/{freeId}")
    public ResponseEntity<String> modify(
            @PathVariable Long freeId,
            @Valid @RequestPart("freeDto") FreeDto freeDto,
            BindingResult bindingResult,
            @RequestPart("imgFile") List<MultipartFile> imgFiles,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력값을 확인해주세요.");
        }

        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        FreeDto origin = freeService.get(freeId);

        Role role = memberService.getMemberByEmail(email).getRole();
        if (!origin.getMemberId().equals(currentUserId) && role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        freeDto.setFreeId(freeId);
        freeDto.setMemberId(currentUserId);

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

    // 게시글 삭제 - 작성자, 관리자 둘 다 가능
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/remove/{freeId}")
    public ResponseEntity<String> remove(@PathVariable Long freeId, Principal principal) {
        if (principal == null) {
            log.info("비회원이 게시글 삭제 시도 - freeId: {}", freeId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();

        FreeDto dto = freeService.get(freeId);
        Long postOwnerId = dto.getMemberId();

        if (!currentUserId.equals(postOwnerId) && role != Role.ADMIN) {
            log.info("삭제 권한 없음 - 요청자 ID: {}, 작성자 ID: {}, role: {}", currentUserId, postOwnerId, role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        dto.setMemberId(postOwnerId); // memberId 넣어줘야 delete 조건에 맞음

        boolean result = freeService.remove(dto);

        return result
                ? ResponseEntity.ok("게시글이 삭제되었습니다.")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
    }
}
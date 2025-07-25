package com.ecovery.service;

import com.ecovery.domain.*;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.mapper.FreeImgMapper;
import com.ecovery.mapper.FreeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 무료나눔 게시글 서비스
 * 게시글 등록, 게시글 조회(목록, 상세), 게시글 수정, 게시글 삭제, 조회수 증가
 * 페이징 처리 등의 비지니스 로직을 처리하는 클래스
 * MyBatis의 FreeMapper를 주입받아 데이터베이스와 연동
 *
 * @author : yeonsu
 * @fileName : FreeServiceImpl
 * @since : 250717
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeServiceImpl implements FreeService {

    // 의존성 주입, @RequiredArgsConstructor에 의해 생성자 주입이 자동으로 처리됨
    private final FreeMapper freeMapper;
    private final FreeImgMapper freeImgMapper;
    private final FreeImgService freeImgService;
    private final MemberService memberService;

    /** DTO → VO 변환 (DB용) */
    private FreeVO dtoToVo(FreeDto dto) {
        FreeVO vo = new FreeVO();
        vo.setFreeId(dto.getFreeId());
        vo.setTitle(dto.getTitle());
        vo.setContent(dto.getContent());
        vo.setCategory(dto.getCategory());
        vo.setRegionGu(dto.getRegionGu());
        vo.setRegionDong(dto.getRegionDong());
        vo.setItemCondition(dto.getItemCondition());
        vo.setDealStatus(dto.getDealStatus());
        vo.setMemberId(dto.getMemberId());
        return vo;
    }

    /** VO → DTO 변환 (응답용) */
    private FreeDto voToDto(FreeVO vo) {
        log.info("voToDto 호출 - vo: {}", vo);

        FreeDto dto = new FreeDto();
        dto.setFreeId(vo.getFreeId());
        dto.setTitle(vo.getTitle());
        dto.setContent(vo.getContent());
        dto.setCategory(vo.getCategory());
        dto.setRegionGu(vo.getRegionGu());
        dto.setRegionDong(vo.getRegionDong());
        dto.setItemCondition(vo.getItemCondition());
        dto.setDealStatus(vo.getDealStatus());
        dto.setMemberId(vo.getMemberId());
        dto.setNickname(vo.getNickname());
        dto.setViewCount(vo.getViewCount());
        dto.setCreatedAt(vo.getCreatedAt());

        log.info("변환된 DTO: {}", dto);
        return dto;
    }

    // 게시글 등록
    @Override
    public Long register(FreeDto dto, List<MultipartFile> imgFileList) throws Exception {

        // 이미지가 없으면 예외 발생
        if (imgFileList == null || imgFileList.isEmpty()) {
            throw new IllegalArgumentException("이미지는 최소 1장 이상 등록해야 합니다.");
        }

        //FreeDto -> FreeVO 변환
        FreeVO free = dtoToVo(dto);

        //게시글 정보저장
        freeMapper.insert(free);

        if(free.getFreeId() == null) {
            throw new RuntimeException("게시글 ID가 null입니다.");
        }

        //게시글 이미지 저장
        for(int i=0; i<imgFileList.size(); i++) {
            MultipartFile multipartFile = imgFileList.get(i);

            FreeImgVO freeImg = FreeImgVO.builder()
                    .freeId(free.getFreeId())
                    .build();

            //대표 이미지 설정
            if(i == 0){
                freeImg.setRepImgYn("Y");
            }else {
                freeImg.setRepImgYn("N");
            }

            freeImgService.saveFreeImg(freeImg, multipartFile);
        }

        return free.getFreeId();
    }

    // 게시글 목록 조회 (페이징 포함)
    @Override
    public List<FreeDto> getAll(Criteria cri) {
        log.info("전체 게시글 조회: {}", cri);
        List<FreeDto> voList = freeMapper.getListWithPaging(cri);
        return freeMapper.getListWithPaging(cri);
    }

    // 게시글 상세 조회
    @Override
    public FreeDto get(Long freeId) {
        log.info("게시글 상세 조회(freeId) : {}", freeId);

        FreeVO vo = freeMapper.read(freeId);
        log.info("mapper에서 조회된 vo: {}", vo); // vo 결과 확인

        if (vo == null) return null;

        FreeDto dto = voToDto(vo);
        log.info("vo -> dto 변환 후 결과 : {}", dto); // 변환된 DTO 확인

        // 이미지 목록 조회 (VO)
        List<FreeImgVO> imgVoList = freeImgMapper.getFreeImgList(freeId);
        log.info("이미지 리스트 조회 결과 : {}", imgVoList); // 이미지도 확인

        // VO → DTO 변환
        List<FreeImgDto> imgDtoList = imgVoList.stream()
                .map(voImg -> {
                    FreeImgDto imgDto = new FreeImgDto();
                    imgDto.setFreeImgId(voImg.getFreeImgId());
                    imgDto.setFreeId(voImg.getFreeId());
                    imgDto.setImgName(voImg.getImgName());
                    imgDto.setOriImgName(voImg.getOriImgName());
                    imgDto.setImgUrl(voImg.getImgUrl());
                    imgDto.setRepImgYn(voImg.getRepImgYn());
                    return imgDto;
                })
                .toList();

        dto.setImgList(imgDtoList);

        return dto;
    }

    // 게시글 수정
    @Override
    public boolean modify(FreeDto dto, List<MultipartFile> imgFileList) throws Exception {

        // 이미지가 없으면 예외 발생
        if (imgFileList == null || imgFileList.isEmpty()) {
            throw new IllegalArgumentException("이미지는 최소 1장 이상 등록해야 합니다.");
        }

        // FreeDto -> FreeVO 변환
        FreeVO free = dtoToVo(dto);

        // 게시글 정보 수정
        int updatedFreeCount = freeMapper.update(free);

        // 수정된 이미지 수
        int updatedImgCount = 0;

        // 이미지 ID 목록 (FreeDto의 imgList에서 꺼냄)
        List<FreeImgDto> imgDtoList = dto.getImgList();

        for (int i = 0; i < imgFileList.size(); i++) {
            MultipartFile multipartFile = imgFileList.get(i);
            FreeImgDto imgDto = imgDtoList.get(i);

            // FreeImgVO 생성
            FreeImgVO imgVo = FreeImgVO.builder()
                    .freeImgId(imgDto.getFreeImgId())  // 기존 이미지 ID
                    .freeId(dto.getFreeId())
                    .build();

            // 대표 이미지 설정
            if (i == 0) {
                imgVo.setRepImgYn("Y");
            } else {
                imgVo.setRepImgYn("N");
            }

            // 이미지 수정
            boolean isUpdated = freeImgService.updateFreeImg(imgVo, multipartFile);

            if (isUpdated) {
                updatedImgCount++;
            }
        }

        return (updatedFreeCount == 1) && (updatedImgCount == imgFileList.size());
    }


    // 게시글 삭제
    @Override
    public boolean remove(FreeDto dto) {
        log.info("게시글 삭제 요청: {}", dto);
        FreeVO vo = dtoToVo(dto);
        return freeMapper.delete(vo) == 1;
    }

    // 전체 게시글 수 조회 (페이징)
    @Override
    public int getTotalCount(Criteria cri) {
        log.info("전체 게시글 수 조회");
        return freeMapper.getTotalCount(cri);
    }

    // 조회수 증가
    @Override
    public void updateViewCount(Long freeId) {
        log.info("조회수 증가(freeId) : {}", freeId);
        freeMapper.updateViewCount(freeId);
    }



}

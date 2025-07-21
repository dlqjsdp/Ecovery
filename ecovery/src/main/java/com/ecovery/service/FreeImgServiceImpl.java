package com.ecovery.service;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.mapper.FreeImgMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 무료나눔 이미지 서비스 구현체
 * - 무료나눔 게시글에 연결된 이미지의 등록, 조회, 수정, 삭제 기능을 담당
 * - 실제 이미지 파일은 서버에 저장하고, DB에는 파일명과 경로 정보를 저장
 * - 게시글 삭제 시 DB는 외래키 ON DELETE CASCADE로 이미지 자동 삭제 처리
 * - 파일 삭제는 직접 수행 (서버의 실제 이미지 파일 제거)
 *
 * @author : yeonsu
 * @fileName : FreeImgServiceImpl
 * @since : 250721
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeImgServiceImpl implements FreeImgService {

    private final FreeImgMapper freeImgMapper;
    private final FileService fileService;

    private static final String uploadPath = "C:/ecovery/free";

    // 이미지 등록
    @Override
    public void register(FreeImgVO imgVO) {
        try {
            // 파일 업로드는 controller에서 완료됐다고 가정
            freeImgMapper.insert(imgVO);
        } catch (Exception e) {
            log.error("이미지 DB 등록 중 오류 발생", e);
        }
    }

    // 게시글에 연결된 이미지 전체 조회
    @Override
    public List<FreeImgDto> getAll(Long freeId) {
        return freeImgMapper.getFreeImgList(freeId);
    }

    // 이미지 수정 (기존 파일 삭제 후 DB 갱신)
    @Override
    public boolean modify(FreeImgVO imgVO) {
        try {
            // 기존 대표 이미지 정보를 가져옴
            FreeImgDto existing = freeImgMapper.getById(imgVO.getFreeId());
            if (existing != null) {
                String oldPath = uploadPath + "/" + existing.getImgName();
                fileService.deleteFile(oldPath); // 실제 파일 삭제
            }

            // DB 업데이트 (imgVO에는 새로운 이미지 정보가 들어있음)
            return freeImgMapper.update(imgVO) == 1;

        } catch (Exception e) {
            log.error("이미지 수정 중 오류 발생", e);
            return false;
        }
    }

    // 게시글에 연결된 이미지 전체 삭제 (파일만 수동 삭제, DB는 FK Cascade로 자동 삭제)
    @Override
    public void removeAll(Long freeId) {
        List<FreeImgDto> imgList = freeImgMapper.getFreeImgList(freeId);
        // 연결된 이미지 각각 삭제
        for (FreeImgDto img : imgList) {
            try {
                String filePath = uploadPath + "/" + img.getImgName();
                fileService.deleteFile(filePath);
            } catch (Exception e) {
                log.warn("이미지 파일 삭제 실패: {}", img.getImgName(), e);
            }
        }
        // DB 삭제는 생략 (ON DELETE CASCADE로 자동)
    }

    // 특정 이미지 한 장 삭제
    @Override
    public boolean remove(Long freeImgId) {
        // DB에서 이미지 정보 조회 (파일 경로를 알아내기 위함)
        FreeImgDto img = freeImgMapper.getById(freeImgId);
        if (img == null)
            return false;

        try {
            // 실제 파일 삭제
            String filePath = uploadPath + "/" + img.getImgName();
            fileService.deleteFile(filePath);
        } catch (Exception e) {
            log.warn("이미지 파일 삭제 실패: {}", img.getImgName(), e);
        }

        // DB에서 이미지 삭제 (1이면 성공)
        return freeImgMapper.delete(freeImgId) == 1;
    }

    // 이미지 단건조회
    @Override
    public FreeImgDto getById(Long freeImgId) {
        return freeImgMapper.getById(freeImgId);
    }

}

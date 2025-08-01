package com.ecovery.service;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.domain.ItemImgVO;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.mapper.FreeImgMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/*
 * 무료나눔 이미지 서비스 구현 클래스
 * - 게시글에 연결된 이미지의 등록, 수정, 삭제, 조회 기능을 담당
 * - 이미지 파일은 서버의 지정된 경로에 저장되며, DB에는 파일 경로 및 정보만 저장
 * - 다중 이미지 업로드 및 대표 이미지(썸네일) 지정 기능 포함
 * - 삭제 시 DB에서 제거되고, 서버의 이미지 파일도 함께 삭제됨
 * - 게시글 삭제 시 이미지는 외래키 제약 조건(ON DELETE CASCADE)으로 자동 삭제
 * - 단, 서버의 실제 이미지 파일은 직접 삭제 처리함
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

    // application.properties에서 불러오기
    @Value("${freeImgLocation}")
    private String freeImgLocation;


    /*
    * 이미지 등록
    * 파일명을 UUID로 저장하고, DB에는 경로와 원본명 함께 저장
    */
    @Override
    public void saveFreeImg(FreeImgVO freeImgVO, MultipartFile freeImgFile) throws Exception {
        String oriImgName = freeImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 이미지 수 제한 체크
        List<FreeImgVO> existingImages = freeImgMapper.getFreeImgList(freeImgVO.getFreeId());
        if (existingImages.size() >= 5) {
            throw new IllegalStateException("이미지는 최대 5장까지만 등록할 수 있습니다.");
        }

        // 대표 이미지 중복 제한
        if ("Y".equals(freeImgVO.getRepImgYn())) {
            boolean hasRep = existingImages.stream()
                    .anyMatch(img -> "Y".equals(img.getRepImgYn()));

            if (hasRep) {
                throw new IllegalStateException("대표 이미지는 1장만 등록할 수 있습니다.");
            }
        }

        // 파일 저장
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(freeImgLocation, oriImgName, freeImgFile.getBytes());
            imgUrl = "/images/free/" + imgName;
        }

        // VO 세팅
        freeImgVO.setOriImgName(oriImgName);
        freeImgVO.setImgName(imgName);
        freeImgVO.setImgUrl(imgUrl);

        // DB 저장
        freeImgMapper.insert(freeImgVO);
    }

    /*
     * 이미지 수정
     * 기존 파일 삭제 후, 새로운 파일로 업데이트
     */
    @Override
    public boolean updateFreeImg(FreeImgVO freeImgVO, MultipartFile freeImgFile) throws Exception {
        if (freeImgFile.isEmpty()) {
            return false;
        }

        // 기존 이미지 조회
        FreeImgDto savedImg = freeImgMapper.getById(freeImgVO.getFreeImgId());

        // 기존 이미지 삭제
        if (!StringUtils.isEmpty(savedImg.getImgName())) {
            fileService.deleteFile(freeImgLocation + "/" + savedImg.getImgName());
        }

        // 새 이미지 저장
        String oriImgName = freeImgFile.getOriginalFilename();
        String imgName = fileService.uploadFile(freeImgLocation, oriImgName, freeImgFile.getBytes());
        String imgUrl = "/images/free/" + imgName;

        // VO 업데이트
        freeImgVO.setOriImgName(oriImgName);
        freeImgVO.setImgName(imgName);
        freeImgVO.setImgUrl(imgUrl);

        int result = freeImgMapper.update(freeImgVO);
        return result == 1;
    }

    // 이미지 삭제
    @Override
    @Transactional
    public void deleteFreeImg(Long freeImgId) throws Exception {

        // 삭제 대상 이미지 조회
        FreeImgDto img = freeImgMapper.getById(freeImgId);
        if (img == null) {
            throw new IllegalArgumentException("삭제할 이미지가 존재하지 않습니다.");
        }

        // 실제 파일 삭제
        if (!StringUtils.isEmpty(img.getImgName())) {
            fileService.deleteFile(freeImgLocation + "/" + img.getImgName());
        }

        // DB 삭제
        int deleted = freeImgMapper.delete(freeImgId);
        if (deleted != 1) {
            throw new IllegalStateException("DB에서 이미지 삭제 실패");
        }

        // 대표이미지였는지 확인 → 남은 이미지 중 하나를 대표로 자동 지정
        if ("Y".equals(img.getRepImgYn())) {
            List<FreeImgVO> remaining = freeImgMapper.getFreeImgList(img.getFreeId());

            if (!remaining.isEmpty()) {
                Long newRepImgId = remaining.get(0).getFreeImgId();
                freeImgMapper.setRepImg(newRepImgId); // ← Mapper에 추가 필요!
            }
        }
    }

    // 다중 이미지 업로드
    @Override
    public void saveAllFreeImages(Long freeId, List<MultipartFile> imgFiles) throws Exception {
        if (imgFiles == null || imgFiles.isEmpty()) return;

        List<FreeImgVO> existing = freeImgMapper.getFreeImgList(freeId);
        int remain = 5 - existing.size();

        if (imgFiles.size() > remain) {
            throw new IllegalStateException("최대 5장까지만 등록 가능합니다.");
        }

        for (int i = 0; i < imgFiles.size(); i++) {
            MultipartFile file = imgFiles.get(i);

            FreeImgVO vo = new FreeImgVO();
            vo.setFreeId(freeId);

            // 첫 번째 이미지만 대표 이미지로 설정 (기존 대표 없을 경우)
            boolean hasRep = existing.stream().anyMatch(img -> "Y".equals(img.getRepImgYn()));
            vo.setRepImgYn((i == 0 && !hasRep) ? "Y" : "N");

            saveFreeImg(vo, file);
        }
    }

    // 대표 이미지 조회 (썸네일)
    @Override
    public FreeImgDto getRepImg(Long freeId) {
        return freeImgMapper.getRepImg(freeId);
    }

    // 게시글에  연결된 전체 이미지 조회
    @Override
    public List<FreeImgVO> getAll(Long freeId) {
        return freeImgMapper.getFreeImgList(freeId);
    }

    @Override
    public void deleteAllByFreeId(Long freeId) throws Exception {
        // 1. freeId에 해당하는 모든 이미지 목록을 가져옴
        List<FreeImgVO> imgList = freeImgMapper.getFreeImgList(freeId);

        if (imgList != null && !imgList.isEmpty()) {
            // 2. 이미지 파일을 물리적으로 삭제
            for (FreeImgVO img : imgList) {
                // 이미지 파일 삭제 로직 호출 (예: FileSystem.delete(img.getImgUrl()))
                // 여기에 파일 삭제 코드를 직접 구현하거나 별도의 파일 삭제 유틸리티를 사용해야 합니다.
                // 이 예시에서는 생략합니다.
            }

            // 3. DB에서 모든 이미지 레코드 삭제
            freeImgMapper.deleteAllByFreeId(freeId); // 이 메서드는 Mapper에 직접 구현해야 합니다.
        }
    }
}




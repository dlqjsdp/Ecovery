package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.mapper.EnvMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 환경톡톡 게시글 서비스 구현 클래스
 * 게시글 CRUD 및 목록 조회(페이징)을 DTO로 처리하며, 내부에서는 EnvVO를 사용하여 EnvMapper와 연결
 * Service 계층에서 비즈니스 로직을 담당하며 컨트롤러에 결과를 반환
 * @author : yukyeong
 * @fileName : EnvServiceImpl.java
 * @since : 250715
 * @history
     - 250715 | yukyeong | EnvServiceImpl 클래스 최초 작성 (CRUD 구현)
     - 250716 | yukyeong | 게시글 목록 조회 (페이징 포함), 게시글 총 개수 조회, 조회수 증가 추가
     - 250717 | yukyeong | DTO ↔ VO 변환 메서드 추가
     - 250718 | yukyeong | DTO 기반 서비스로 전환
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvServiceImpl implements EnvService {

    // EnvMapper는 DB 연동을 위한 MyBatis 인터페이스
    private final EnvMapper envMapper;

    // DTO를 VO로 변환하는 메서드 (DB 작업용으로 변환)
    private EnvVO dtoToVo(EnvDto envDto) {
        EnvVO env = new EnvVO();
        env.setEnvId(envDto.getEnvId());
        env.setTitle(envDto.getTitle());
        env.setContent(envDto.getContent());
        env.setMemberId(envDto.getMemberId());
        return env;
    }

    // VO를 DTO로 변환하는 메서드 (컨트롤러 또는 뷰로 전달용)
    private EnvDto voToDto(EnvVO env) {
        EnvDto envDto = new EnvDto();
        envDto.setEnvId(env.getEnvId());
        envDto.setTitle(env.getTitle());
        envDto.setContent(env.getContent());
        envDto.setMemberId(env.getMemberId());
        envDto.setNickname(env.getNickname());
        envDto.setViewCount(env.getViewCount());
        envDto.setCreatedAt(env.getCreatedAt());
        envDto.setUpdatedAt(env.getUpdatedAt());
        return envDto;
    }

    // 게시글 등록 (DTO → VO 변환 후 Mapper 호출)
    // DTO를 VO로 변환 후 insert 쿼리 실행, 생성된 ID를 다시 DTO에 설정
    @Override
    public void register(EnvDto envDto) {
        log.info("register() - 게시글 등록");
        EnvVO env = dtoToVo(envDto);
        envMapper.insert(env);
        envDto.setEnvId(env.getEnvId()); // DB에서 생성된 ID를 DTO에 반영
    }

    // 게시글 단건 조회
    // 조회된 VO를 DTO로 변환하여 반환
    @Override
    public EnvDto get(Long envId) {
        log.info("get() - 게시글 단건 조회");
        EnvVO env = envMapper.read(envId);
        if (env == null) return null; // null 체크 추가
        return voToDto(env);
    }

    // 게시글 수정
    // DTO를 VO로 변환하여 update 쿼리 실행, 결과가 1이면 true 반환
    @Override
    public boolean modify(EnvDto envDto) {
        log.info("modify() - 게시글 수정");
        EnvVO env = dtoToVo(envDto);
        return envMapper.update(env) == 1; // update() 실행 시 영향받은 행 수가 1이면 true
    }

    // 게시글 삭제
    // 전달받은 ID로 delete 쿼리 실행, 결과가 1이면 true 반환
    @Override
    public boolean remove(Long envId) {
        log.info("remove() - 게시글 삭제");
        return envMapper.delete(envId) == 1;
    }

    // 게시글 목록 조회 (페이징 포함)
    // VO 목록을 DTO 목록으로 변환하여 반환
    @Override
    public List<EnvDto> getList(Criteria cri) {
        log.info("getList() - 게시글 목록 조회");
        List<EnvVO> envList = envMapper.getListWithPaging(cri);

        // Stream API로 VO 리스트 → DTO 리스트 변환
        return envList.stream()
                .map(this::voToDto)
                .collect(Collectors.toList());
    }

    // 전체 게시글 수 조회 (페이징 처리에 필요)
    @Override
    public int getTotal(Criteria cri) {
        log.info("getTotal() - 게시글 총 개수 조회");
        return envMapper.getTotalCount(cri);
    }

    // 게시글 조회수 증가
    // 주어진 ID에 해당하는 게시글의 viewCount +1 처리
    @Override
    public void increaseViewCount(Long envId){

        envMapper.updateViewCount(envId);
    }
}

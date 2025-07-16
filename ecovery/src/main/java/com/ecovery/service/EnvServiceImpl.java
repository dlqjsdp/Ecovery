package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.mapper.EnvMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 환경톡톡 게시글 서비스 구현 클래스
 * 게시글 CRUD 및 목록 조회(페이징) 기능을 EnvMapper와 연결하여 처리
 * Service 계층에서 비즈니스 로직을 담당하며 컨트롤러에 결과를 반환
 * @author : yukyeong
 * @fileName : EnvServiceImpl.java
 * @since : 250715
 * @history
     - 250715 | yukyeong | EnvServiceImpl 클래스 최초 작성 (CRUD 구현)
     - 250716 | yukyeong | 게시글 목록 조회 (페이징 포함), 게시글 총 개수 조회, 조회수 증가 추가
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvServiceImpl implements EnvService {

    // 의존성 주입, @RequiredArgsConstructor에 의해 생성자 주입이 자동으로 처리됨
    private final EnvMapper envMapper;

    /**
     * 게시글 등록
     * EnvMapper의 insert() 메서드를 호출하여 게시글을 DB에 저장
     * @param env 등록할 게시글 정보 (EnvVO)
     */
    @Override
    public void register(EnvVO env) {
        log.info("register() - 게시글 등록");
        envMapper.insert(env);
    }

    /**
     * 게시글 단건 조회
     * envId에 해당하는 게시글 1건을 조회
     * @param envId 조회할 게시글 ID
     * @return EnvVO 게시글 정보 (존재하지 않을 경우 null)
     */
    @Override
    public EnvVO get(Long envId) {
        log.info("get() - 게시글 단건 조회");
        return envMapper.read(envId);
    }

    /**
     * 게시글 수정
     * EnvMapper의 update() 호출 결과로 수정 성공 여부 반환
     * @param env 수정할 게시글 정보
     * @return boolean 수정 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    public boolean modify(EnvVO env) {
        log.info("modify() - 게시글 수정");
        return envMapper.update(env) == 1; // update() 실행 시 영향받은 행 수가 1이면 true
    }

    /**
     * 게시글 삭제
     * EnvMapper의 delete() 호출 결과로 삭제 성공 여부 반환
     * @param envId 삭제할 게시글 ID
     * @return boolean 삭제 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    public boolean remove(Long envId) {
        log.info("remove() - 게시글 삭제");
        return envMapper.delete(envId) == 1;
    }

    /**
     * 게시글 목록 조회 (페이징 포함)
     * Criteria 객체를 이용해 조회 범위(offset, amount)를 지정
     * @param cri Criteria 페이징 및 검색 조건
     * @return List<EnvVO> 조회된 게시글 목록
     */
    @Override
    public List<EnvVO> getList(Criteria cri) {
        log.info("getList() - 게시글 목록 조회");
        return envMapper.getListWithPaging(cri);
    }

    /**
     * 게시글 총 개수 조회
     * 페이징 처리에 필요한 전체 레코드 수를 조회
     * @param cri Criteria 페이징 및 검색 조건
     * @return int 총 게시글 수
     */
    @Override
    public int getTotal(Criteria cri) {
        log.info("getTotal() - 게시글 총 개수 조회");
        return envMapper.getTotalCount(cri);
    }

    /**
     * 게시글 조회수 1 증가
     * 주어진 게시글 ID에 해당하는 view_count 값을 +1 처리
     * @param envId 조회수를 증가시킬 게시글 ID (PK)
     */
    @Override
    public void increaseViewCount(Long envId){
        envMapper.updateViewCount(envId);
    }
}

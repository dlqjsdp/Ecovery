package com.ecovery.service;

import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.mapper.FreeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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


    //게시글 등록
    @Override
    public void register(FreeVO vo){
        log.info("게시글 등록 : {}", vo);
        freeMapper.insert(vo);
    }

    //전체 게시글 조회(페이징처리 포함)
    @Override
    public List<FreeDto> getAll(Criteria cri) {
        log.info("전체 게시글 조회: {}", cri);
        return freeMapper.getListWithPaging(cri);
    }

    //게시글 상세조회
    @Override
    public FreeDto get(Long freeId) {
        log.info("게시글 상세 조회(freeId) : {}", freeId);
        return freeMapper.read(freeId);
    }

    // 게시글 수정
    @Override
    public boolean modify(FreeVO vo) {
        log.info("게시글 수정 : {}", vo);
        return freeMapper.update(vo) == 1;
    }

    // 게시글 삭제
    @Override
    public boolean remove(FreeVO vo) {
        log.info("게시글 삭제 요청 : {}", vo);
        int deletedCount = freeMapper.delete(vo);
        return deletedCount == 1;
    }

    // 전체 게시글 수 조회 (페이징 처리를 위한)
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

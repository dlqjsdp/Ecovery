package com.simplane.mapper;

import com.simplane.domain.BoardVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    // 데이터 수정
    public Long update(BoardVO vo);
}

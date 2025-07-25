package com.ecovery.mapper;

import com.ecovery.dto.DisposalHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class DisposalHistoryMapperTest {

    @Autowired
    private DisposalHistoryMapper mapper;

    @Test
    public void testRead() {
        Long targetId=57L;

        DisposalHistoryDto dto = mapper.findByDisposalHistoryId(targetId);

        if(dto!=null) {
            log.info("조회 결과 : {}", dto.toString());
        }else {
            log.info("조회 실패했습니다.");
        }
    }

    @Test
    public void testReadByMemberId() {
        Long targetId=1L;

        List<DisposalHistoryDto> dto = mapper.findDisposalHistoryWithImgByMemberId(targetId);

        if(dto!=null) {
            log.info("조회 결과 : {}", dto.toString());
        }else {
            log.info("조회 실패했습니다.");
        }
    }

    @Test
    public void testReadAll() {
        List<DisposalHistoryDto> dtos = mapper.findAllDisposalHistory();

        if(dtos!=null) {
            log.info("조회 결과 : {}", dtos.toString());
        }else {
            log.info("조회 실패했습니다.");
        }
    }
}

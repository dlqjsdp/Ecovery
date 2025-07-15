package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryImgVO;
import com.ecovery.domain.DisposalHistoryVO;
import com.ecovery.dto.DisposalHistoryImgDto;
import com.ecovery.mapper.DisposalHistoryImgMapper;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DisposalHistoryImgServiceImpl implements DisposalHistoryImgService{

    private final DisposalHistoryImgMapper disposalHistoryImgMapper;


    @Override
    public void saveDisposalImg(DisposalHistoryImgDto disposalHistoryImgDto) {
        DisposalHistoryImgVO disposalHistoryImgVO = disposalHistoryImgDto.toDisposalHistoryImgVO();
        disposalHistoryImgMapper.insertDisposalImg(disposalHistoryImgVO);
    }

    @Override
    public DisposalHistoryImgDto getDisposalImg(Long disposalHistoryId) {
        DisposalHistoryImgVO disposalHistoryImgVO = disposalHistoryImgMapper.findDisposalImgByDisposalHistoryId(disposalHistoryId);

        return DisposalHistoryImgDto.builder()
                .disposalImgId(disposalHistoryImgVO.getDisposalImgId())
                .disposalImgName(disposalHistoryImgVO.getDisposalImgName())
                .disposalImgUrl(disposalHistoryImgVO.getDisposalImgUrl())
                .disposalHistoryId(disposalHistoryImgVO.getDisposalHistoryId())
                .oriDisposalImgName(disposalHistoryImgVO.getOriDisposalImgName())
                .build();
    }
}

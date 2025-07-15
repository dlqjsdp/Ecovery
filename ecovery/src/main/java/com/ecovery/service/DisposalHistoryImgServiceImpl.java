package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryImgVO;
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
    public void saveImg(DisposalHistoryImgVO disposalHistoryImgVO) {
        disposalHistoryImgMapper.insertDisposalImg(disposalHistoryImgVO);
    }

    @Override
    public DisposalHistoryImgVO getHistoryImg(Long disposalHistoryId) {
        return disposalHistoryImgMapper.findDisposalImgByDisposalHistoryId(disposalHistoryId);
    }
}

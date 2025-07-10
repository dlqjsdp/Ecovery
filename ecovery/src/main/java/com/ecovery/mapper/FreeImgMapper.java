package com.ecovery.mapper;

import com.ecovery.dto.FreeImgDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FreeImgMapper {

    public void insert(FreeImgDTO freeImgdto);


}

package com.example.shop.dto;

import com.example.shop.entity.ItemImg;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

//상품 정보를 조회해서 화면에 전달할 때 사용하는 Dto

@Getter
@Setter
public class ItemImgDto {

    private Long id;

    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 명

    private String imgUrl; //이미지 조회 경로

    private String repimgYn; //대표 이미지 여부

    private static ModelMapper modelMapper = new ModelMapper(); //ModelMapper 맴버 변수 추가

    //ItemImg 객체를 전달 받아서, modelMapper이 ItemImg 객체를 ItmeImgDto객체로 자동 변환 -> modelMapper를 이용해 DTO의 데이터를 엔티티로 자동 복사
    public static ItemImgDto of(ItemImg itemImg) {

        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}

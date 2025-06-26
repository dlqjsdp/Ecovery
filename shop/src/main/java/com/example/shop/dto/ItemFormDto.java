package com.example.shop.dto;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.Item;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

//상품 데이터 정보를 전달하는 DTO

@Getter
@Setter
public class ItemFormDto {

    private Long id; //상품코드

    @NotBlank(message = "상품명은 필수 입력 값입니다.") //NotBlank : null 값 체크 + 문자열의 경우 빈 문자열인지 검사(String 전용, 공백만 있어도 불가)
    private String itemNm; //상품명

    @NotNull(message = "가격은 필수 입력 값입니다.") //NotNull : nill이 아닌지만 검사함(공백문자열, 빈 문자열 허용)
    private Integer price; //가격

    @NotBlank(message = "상세설명은 필수 입력 값입니다.")
    private String itemDetail; //상세설명

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber; //재고

    private ItemSellStatus itemSellStatus; //상품 판매 상태

    //상품 이미지를 리스트로 저장
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //상품 이미지(여러장이니 List)

    /* ItemImg entity에 item_img_id값을 리스트로 가지고 있다.
       이미지 수정 시 특정 상품에서 전체 이미지를 수정하지 않고, 개별 이미지를 쉬정하기 위한 용도로 item_img_id를 가지고 있기 위해서
     */
    private List<Long> itemImgIds = new ArrayList<>(); //상품 ID값

    private static ModelMapper modelMapper = new ModelMapper();

    //ItemFormDto(자기 자신)을 Itme으로 변환
    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }

    //Item -> ItemFormDto 변환
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }
}

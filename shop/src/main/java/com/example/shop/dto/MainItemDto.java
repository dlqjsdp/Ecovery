package com.example.shop.dto;

//메인 화면에서 상품을 보여줄 때 사용할 DTO

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

//메인 페이지에서 상품을 보여줄 때 사용할 DTO 클래스
//DB의 Item + ItemImg 테이블에서 필요한 정보만 골라서 사용자에게 보여주기 좋게 만든 데이터 상자

//해당 DTO 클래스를 따로 쓰는 이유 : Entity는 너무 많은 정보를 가지고 있어서 화면에 딱 필요한 정보만 전달하려면 DTO로 가볍게 만드는게 좋음

@Setter
@Getter
public class MainItemDto {
    
    //클래스 정의
    //메인 페이지에 상품 리스트로 출력할 수 있음
    private Long id; //상품 ID
    private String itemNm; //상품명
    private String itemDetail; //상품 설명
    private String imgUrl; //상품 대표 이미지 URL
    private Integer price; //상품 가격
    
    //QueryProjection을 이용하면 Item 객체로 값을 받은 후 DTO클래스로 변환하는 과정 없이 DTO 객체를 뽑아낼 수 있다
    @QueryProjection //Querydsl에서 조회 나온 결과를 MainItemDto 객체로 전달 받는 어노테이션
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price) {
        //생성자 선언 : 5개의 값을 받아서 각각 클래스 내부의 필드 값에 대입해주는 역할
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}

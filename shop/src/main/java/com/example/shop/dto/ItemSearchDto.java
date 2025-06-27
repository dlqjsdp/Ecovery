package com.example.shop.dto;


import com.example.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

//상품 데이터 조회 시 상품 조회 조건을 가지고 있는 DTO 클래스
//DTO는 "Data Transfer Object"의 줄임말로, 데이터를 주고받을 때 사용하는 우체통 같은 개념

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType; //날짜를 기준으로 상품을 검색할 때 쓰는 조건(예를들어 1d는 하루전, 1w는 일주일 전, 1m은 한달전 // ItemRepositoryCustomImpl 클래스 참고)

    private ItemSellStatus itemSellStatus; //상품의 판매 상태를 저장(SELL, SOLD_OUT)

    private String searchBy; //상품을 조회할 때 어떤 유형으로 데이터를 조회할지 정함(상품명(itemNm), 상품등록자(createBy) 등)

    private String searchQuery = ""; //실제 검색어(키워드)를 담는 칸(상품명, 상품등록지 ID 등)
 }

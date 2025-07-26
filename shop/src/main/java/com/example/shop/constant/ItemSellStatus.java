package com.example.shop.constant;

// 상품이 현재 판매중인지 품절 상태인지를 나타내는 enum 타입 클래스
// 장점 : 연관된 상수들을 모아둘 수 있으며 enum에 정의한 타입만 값을 가지도록 컴파일 시 체크할 수 있음

public enum ItemSellStatus { // enum : 정해진 값을 중 하나만 선택해서 사용할 때 사용
    SELL, // 판매중인 상태
    SOLD_OUT //품절 상태
}

package com.shop.constant;

public enum ItemSellStatus {
    SELL, SOLD_OUT
}
//기본적으로 enum에 열거된 상수들은 추가적인 객체 생성 없이 외부에서 사용 가능하고
// (enum 내부 메소드와는 별개로),
// 불변이기에 상수들 앞에 아무것도 안붙지만 public static final 이다.
// 그리고 기본적으로 final인 상수이기에 모두 대문자로 적는 것을 원칙으로 한다.
// 만들어진 값 외에 다른 값 입력 불가.
// ItemSellStatus itemSellStatus = SELLING; 불가
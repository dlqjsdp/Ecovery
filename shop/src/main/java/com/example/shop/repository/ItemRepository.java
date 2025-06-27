package com.example.shop.repository;

import com.example.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;


//                                      JpaRepository<엔티티 타입 클래스, 기본키 타입>
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom { //ItemRepositoryCustom : Querydsl로 구현한 상품 관리 페이지 목록을 불러오는 메소드 사용 가능

    // Spring Data JPA 인터페이스에서 데이터 조회용 메서드를 정의
    // Item 엔티티를 다양한 기준으로 검색
    List<Item> findByItemNm(String itemNm); // 상품명이 정확히 일치하는 상품 조회
    List<Item> findByItemNmLike(String itemNm); // itemNm이 주어진 문자열과 유사한 값을 가진 상품 조회 : 매개변수에 %를 직접 포함하여 넘겨줘야 함
    List<Item> findByPriceLessThan(int price); // 전달한 price보다 작은 가격의 상품 목록 조회

    // @Query를 사용해서 직접 SQL을 작성한 예제

    // JPQL 방식 : JPQL -> entity 객체 이용
    // 반드시 애칭 필요, itemDetail : 엔티티에 기재된 이름으로 기재
    // Item 엔티티 이름, i는 별칭, itemDetail 엔티티 필드, :itemDetail 파라미터 바인딩, 가격 내림차순 정령
    // 특징 : 엔티티 필드명을 기준으로 작성, 별칭 필수 사용, @Param 이름 일치
    // %:itemDetail% 전달 받은 값을 @Param("itemDetail")에 그대로 기재
    // itemDetail : 오타 나면 바로 빨간줄 표시로 바로 확인이 쉬움
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    // Native SQL 방식 : navtivQuery -> db에 있는 table 이용 // 거의 사용 안함
    // item 실제 DB테이블 이름, item_detail, price는 테이블의 실제 컬럼명
    // item_detail : 오타 났어도 DB에 방문 전까지는 오타인지 찾을 수 없음
    @Query(value = "select * from item where item_detail " + "like %:itemDetail% order by price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}

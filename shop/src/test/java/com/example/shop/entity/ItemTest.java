package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.repository.ItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ItemTest {

    @Autowired
    private ItemRepository itemRepository; //Item 엔티티용 레포지토리 인터페이스 주입

    @Autowired
    EntityManager em; //JPA 핵심 객체인 엔티티 매니저 주입

    JPAQueryFactory queryFactory; //Querydsl에서 JPQL 쿼리를 타입 안정성 있게 만들 수 있는 객체

    //Querydsl을 사용하기 위한 준비를 테스트 실행 전에 해주는 코드
    @BeforeEach //테스트 하나하나 시작할 때마다 자동으로 실행되는 준비작업
    void setUp() {
        queryFactory = new JPAQueryFactory(em); //이걸 만들면 Querydsl 쿼리를 쓸 수 있게 돼
    }

    //itemNm으로 조회
    @Test
    //itemRepository를 이용하여 테스트
    public void testFindByItemNm() {
        List<Item> items = itemRepository.findByItemNm("틈새라면");

        items.forEach((item -> log.info(item.toString())));
    //    items.forEach(System.out::println); 위에랑 같은 코드
        log.info("---------------QueryDSL---------------");

        //Querydsl를 이용하여 테스트
        QItem qItem = QItem.item; //객체 생성

        List<Item> item2 = queryFactory
                .selectFrom(qItem) //select(qItem) + from(qItem) 합친거
                .where(qItem.itemNm.eq("틈새라면"))
                //.fetchOne() : 단건 조회 시 사용
                .fetch();
        item2.forEach((item -> log.info(item.toString())));
    }

    //And 조건 검색
    @Test
    public void testFindByItemNmAndPrice() {
        QItem qItem = QItem.item;

        List<Item> item = queryFactory
                .selectFrom(qItem)
                .where(
                        //상품명이 틈새라면이고 금액이 1250이거나 많은 상품이면 출력
                        qItem.itemNm.eq("틈새라면"),
                        qItem.price.goe(1250)) //.goe : 1250과 같거나 많다 // .gt : 1250보다 많다
                .fetch();

        log.info(item.toString());
    }

    // or 조건 검색
    @Test
    public void testFindByItemNmDetail() {
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .select(qItem)
                .from(qItem)
                .where(
                        qItem.itemNm.contains("땅콩 초코찰떡파이") //itemNm에 땅콩 초코찰떡파이라는 단어가 포함되어 있거나
                        .or(qItem.itemDetail.contains("먹고 싶다")) //itemDetail에 틈새라면이라는 단어가 포함되어 있는 상품

                )
                .fetch();

        items.forEach((item -> log.info(item.toString())));
    }

    // Enum 조건검색
    @Test
    public void testFindBySellStatus() {
        QItem qItem = QItem.item; //QItem : Querydsl이 자동 생성한 Item 엔티티의 Querydsl 표현

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SOLD_OUT)) //픔절이 상품만 조회
                .fetch();

        items.forEach(item -> log.info(item.toString()));
    }

    // 동적 조건 검색(BooleanBuilder 사용)
    @Test
    public void testDynamicSearch() {
        QItem qItem = QItem.item; ////QItem : Querydsl이 자동 생성한 Item 엔티티의 Querydsl 표현
        BooleanBuilder builder = new BooleanBuilder(); //BooleanBuilder: 여러 조건을 .and() 또는 .or()로 누적해서 쓸 수 있는 도구

        String searchNm = "오므라이스"; //싱품명 오므라이스
        Integer minPrice = 10000; //금액 10000원 초과 건

        //조건을 따로 뽑아낸 코드(실무에서 주로 사용)
        if (searchNm != null) { //상품명이 null 아니면
            builder.and(qItem.itemNm.contains(searchNm)); //오므라이스라는 이름이 포함된 상품만 검색
        }

        if (minPrice != null) { //최소 가격이 null 아니면
            builder.and(qItem.price.gt(minPrice)); //오므라이스 + 가격이 10000초과 인 상품만 조회
        }

        List<Item> items = queryFactory //조건을 담은 builder를 .where에 넣어 최종 쿼리 실행
                .selectFrom(qItem)
                .where(builder)
                .fetch();

        items.forEach(item -> log.info(item.toString()));
    }

    //정렬
    @Test
    public void testSort() {
        QItem qItem = QItem.item;

        //금액이 10000원 이상인 등록된 상품 리스트로 정렬하여 출력
        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.price.gt(10000))
                .orderBy(qItem.price.asc()) //.asc() : 오름차순
                .fetch();

        log.info(items.toString());
    }
    
    //정렬 + 페이징 처리
    @Test
    public void testPaginationAndSort() {

        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.price.gt(10000)) //가격이 10000원 초과인 상품 중
                .orderBy(qItem.price.asc())
                .offset(2) //시작 위치 ?번 인덱스부터, 괄호안 숫자
                .limit(3) //최대 ?개 가져오기, 괄호안 숫자
                .fetch();

        log.info(items.toString());
    }

    //그룹화, 집계함수(count, max, avg 등)
    @Test
    public void testAggregateFunction() {
        QItem qItem = QItem.item;

        List<Tuple> fetch = queryFactory //여러건이라 fetch 사용
                .select(
                        qItem.itemSellStatus, //판매중, 품절 2가지 종류 별로
                        qItem.price.avg() //가격 평균
                )
                .from(qItem)
                .groupBy(qItem.itemSellStatus)
                .fetch();
        
        log.info("판매 상태 : ");
        fetch.stream().forEach(item -> log.info(item.toString()));
    }

    //ItemImg 조회(대표이미지가 있는것만 조회)
    @Test
    public void testItemImg(){
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .selectFrom(qItemImg)
                .where(qItemImg.repimgYn.eq("Y")) //대표이미지가 있는 상품 목록 조회
                .fetch();

        result.forEach(item -> log.info(item.toString())); //ItemImg에 toString() 없어서 null 값으로 출력됨
    }

    //ItemImg, Item Join
    @Test
    public void testJoin(){
        QItem qItem = QItem.item;
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .selectFrom(qItemImg)
                .join(qItemImg.item, qItem) //ItemImg와 Item 조인해서
                .where(qItem.itemNm.contains("안경")) //itemNm에 "안경"이 포함된 이미지 조회
                .fetch();
        log.info(result.toString());
    }

    //ItemImg, Item Join
    @Test
    public void testItemJoin(){
        QItem qItem = QItem.item;
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .select(qItemImg)
                .from()
                .join(qItemImg.item, qItem)
                .where(qItem.itemNm.contains("안경"))
                .fetch();
        log.info(result.toString());
    }

}
package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.dto.QMainItemDto;
import com.example.shop.entity.Item;
import com.example.shop.entity.QItem;
import com.example.shop.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//ItemRepositoryCustom 인터페이스를 구현할 클래스 작성(클래스 명 끝에 꼭 Impl 기재 필요)
//Querydsl을 이용해 관리자용 상품 목록과 사용자 메인화면 상품 목록을 조건별로 검색해서 Page 형태로 반환하는 클래스
//상품의 목록을 뽑아오는 클래스(상품 등록일, 판매 여부, 이름에 바나나가 들어가는지 등의 요청을 조건에 맞게 확인후 리스트로 만들어주는 역할)

@Slf4j
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom { //레포지토리 인터페이스의 기능을 구현한 클래스

    //JPAQueryFactory : Querydsl 쿼리를 만들 도구 클래스, queryFactory : 도구의 변수명
    private JPAQueryFactory queryFactory;

    //EntityManager : JPA에서 DB와 연결되는 핵심 도구
    public ItemRepositoryCustomImpl(EntityManager em) { //EntityManager를 통해 JPAQueryFactory 초기화
        queryFactory = new JPAQueryFactory(em); //JPAQueryFactory 신규 생생 시 EntityManager em 활용하여 DB와 연결하고 그 값을 queryFactory 변수에 대입
    }

    //BooleanExpression : Querydsl에서 사용하는 조건 표현식 타입 (ex. item.status == 'SELL'
    //searchSellStatusEq : 판매 상태 조건 생성
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus); //값이 있으면 비교하고 없으면 조건 없이 null 리턴
    }
    //regDtsAfter : 등록일 기준 조건 생성
    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now(); //현재 시간 기준

        //searchDateType에 따라 현재 시간(dateTime)에서 얼마를 뺄지 결정해주는 조건문
        if(StringUtils.equals("all", searchDateType) || searchDateType == null){ //searchDateType이 all 또는 null이면
            return null; //전체 데이터를 본다
        } else if(StringUtils.equals("1d", searchDateType)){ //조건이 1일전부터 지금까지 등록된 상품만 보고 싶으면
            dateTime = dateTime.minusDays(1); //오늘 날짜에서 1을 뺀다
        } else if(StringUtils.equals("1w", searchDateType)){ //일주일 이내 상품이 보고 싶다면
            dateTime = dateTime.minusWeeks(1); //현재 시간에서 1주를 뺀다
        } else if(StringUtils.equals("1m", searchDateType)){ //한달 이내 상품이 보고 싶다면
            dateTime = dateTime.minusMonths(1); //현재 시간에서 1달을 뺀다
        } else if(StringUtils.equals("6m", searchDateType)){ //6개월 이내 상품이 보고 싶다면
            dateTime = dateTime.minusMonths(6); //현재 시간에서 6개월을 뺀다
        }

        return QItem.item.regTime.after(dateTime); //해당 날짜 이후 등록된 상품만 조회
    }
    //상품명 또는 등록자 기준 검색 조건
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){ //검색 기준으 상품명이면
            return QItem.item.itemNm.contains(searchQuery); //itemNm LIKE %검색어%
        } else if(StringUtils.equals("createdBy", searchBy)){ //검색 기준이 등록자 ID면
            return QItem.item.createdBy.contains(searchQuery); //createdBy LIKE %검색어%
        }

        return null; //해당 조건 없음
    }
    
    //관리재 상품 목록 조회 메서드
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        log.info("itemSearchDto.getSearchDateType() : {}",itemSearchDto.getSearchDateType());
        log.info("itemSearchDto.getItemSellStatus() : {}",itemSearchDto.getItemSellStatus());
        log.info("itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery() : {}",
                itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery());

        QueryResults<Item> results = queryFactory //쿼리 실행 결과를 담는 객체
                .selectFrom(QItem.item) //item 테이블로부터 선택
                .where( //조건을 걸어 상품을 꺼내주는 코드
                        regDtsAfter(itemSearchDto.getSearchDateType()) //날짜조건
                        , searchSellStatusEq(itemSearchDto.getItemSellStatus()) //판매 상태 조건
                        , searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()) //상품명 또는 작성자 조건
                )
                .orderBy(QItem.item.id.desc()) //ID 기준 내림차순(최신순)
                .offset(pageable.getOffset()) //몇 번째부터 가져올지 설정(페이징)
                .limit(pageable.getPageSize()) //몇 개ㅐ 가져올지 설정(한 페이지 당 개수)
                .fetchResults(); //실행해서 결과 가져옴

        List<Item> content = results.getResults(); // 실행 가져온 item 목록
        long total = results.getTotal(); //조건에 맞는 전체 item 개수

        return new PageImpl<>(content, pageable, total); //페이지 형태로 감싸서 반환
    }

    //메인 페이지 상품 목록 조회 메서드
    private BooleanExpression itemNmLike(String searchQuery){ //상품명으로만 검색하는 조건
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.contains(searchQuery); //item LIKE %검색어%
    }

    @Override
    //ItemSearchDto itemSearchDto, Pageable pageable 기재 이휴 : 검색 조건 + 페이징 조건을 동시에 처리하기 위해
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item; //item 테이블
        QItemImg itemImg = QItemImg.itemImg; //itemImg 테이블

        QueryResults<MainItemDto> results = queryFactory
                .select( //필요한 컬럼만 골라서 MainItemDto로 매핑
                        new QMainItemDto( //객체 생성
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                )
                .from(itemImg) //이미지 테이블 기준으로 시작
                .join(itemImg.item, item) //item 테이블과 조인
                .where(itemImg.repimgYn.eq("Y")) //대표 이미지인 것만 조회
                .where(itemNmLike(itemSearchDto.getSearchQuery())) //상품명 검색 조건
                .orderBy(item.id.desc()) //최신순 정려
                .offset(pageable.getOffset()) //몇 번째부터
                .limit(pageable.getPageSize()) //몇 번째까지
                .fetchResults(); //실행

        List<MainItemDto> content = results.getResults(); //가져온 DTO 리스트
        long total = results.getTotal(); //전체 개수

        //content : 조회 내용, pageable : 페이징 개수, total : 전체 개수
        return new PageImpl<>(content, pageable, total); //감싸서 반환
    }
}

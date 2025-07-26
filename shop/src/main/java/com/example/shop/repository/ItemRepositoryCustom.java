package com.example.shop.repository;


import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//interface는 그냥 이런 기능을 만들어 줄꼐요라고 선언만하고 실제 구현은 클래스에서 진행 필요
public interface ItemRepositoryCustom {

    //관리자 페이지에서 상품 목록을 보기 위해, 검색 조건(DTO와) 페이지 번호(페이징)를 받아서,
    // 딱 맞는 상품 목록을 한 페이지로 뽑아주는 기능을 만들겠다
    //itemSearchDto : 어떤 조건으로 검색할지 //pageable : 몇 번째 페이지인지, 판 페이지에 몇 개씩 보여줄지, 정렬 방법 등
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    //메인 페이지에 보여줄 상품 리스트를 가져오는 메소드
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}

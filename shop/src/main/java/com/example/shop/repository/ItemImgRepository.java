package com.example.shop.repository;

import com.example.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//상품의 이미지 정보를 저장하기 위한 레파지토리
//Repository는 DB와 통신하는 역할을 전담하는 인터페이스이고, JPA가 구현체를 자동 생성해주기 때문에 코드 없이도 잘 작동하는 것

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    //item_id가 특정 값인 ItemImg 엔티티들을 모두 찾아서, id 순서로 오름차순 정렬해서 리스트로 반환
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

}

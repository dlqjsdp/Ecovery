package com.example.shop.entity;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

//장바구니와 회원이 정상적으로 연결되었는지 확인하는 테스트

@SpringBootTest
@Transactional //테스트하면서 DB에 넣은거 지우겠습니다.
@Slf4j
class CartTest {
    
    @Autowired
    CartRepository cartRepository; //장바구니 상품을 저장/조회 하기 위한 JPA 리포지토리
    
    @Autowired
    MemberRepository memberRepository; //회원 정보 저장/조회 하기 위한 JPA 리포지토리
    
    @Autowired
    PasswordEncoder passwordEncoder; //비밀번호 암호화
    
    //@PersistenceContext -> @Autowired 가능하나 정확한 명시를 위해서는 @PersistenceContext 기재가 좋음
    @PersistenceContext
    EntityManager em; //영속컨텍스트에서 엔티티를 검색하고 없을 경우 DB에서 데이터를 찾아 영속 컨텍스트에 저장

    //회원 정보를 만드는 도우미 메서드
    public Member createMember() {
        MemberFormDto memberFormDto = MemberFormDto.builder() //MemberFormDto 만들고 이를 기반으로 Member 객체 생성
                .email("teat@email.com")
                .name("꼬돌이")
                .address("서울 송파구")
                .password("1234")
                .build();

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    //장바구니가 회원과 잘 매핑되었는지 확인하는 테스트 @OneToOne
    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember(); //회원 생성
        memberRepository.save(member); //회원 저장

        Cart cart = new Cart(); //장바구니 생성
        cart.setMember(member); //회원과 연결
        cartRepository.save(cart); //장바구기 저장

        em.flush(); //영속컨텍스트에 있는 데이터를 DB에 저장
        em.clear(); //영속컨텍스트 클리어

        Cart saveCart = cartRepository.findById(cart.getId()) //장바구니 ID로 조회해서 Cart 객체 가져옴
                .orElseThrow(() -> new EntityNotFoundException("해당 ID가 존재하지 않아요")); //정보가 없으면 예외 발생

        log.info("savedCart =====> {}", saveCart);

        assertEquals(saveCart.getMember().getId(), member.getId()); //장바구니에 연결된 회원 ID와 원래 저장한 회원 ID가 같은지 확인
    }

}
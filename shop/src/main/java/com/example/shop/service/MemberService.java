package com.example.shop.service;

import com.example.shop.entity.Member;
import com.example.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//회원 가입 + 로그인 인증 처리 로직을 담고 있는 서비스 클래스

@Service
@RequiredArgsConstructor //final이 붙은 필드들을 자동으로 생성자 주입
@Slf4j
// @Transactional : 메서드 또는 클래스에서 트랜잭션을 적용해주는 어노테이션, 트랜잭션이랑 여러 DB 작업을 하나의 묶음으로 처리해서 모두 성공하거나 하나라도 실패하면 전부 롤백 시키는것
//implements UserDetailsService : 보통은 따로 만드는데 서비스에 넣는건 일반적이지 않음
public class MemberService implements UserDetailsService { //스프링 시큐리티에서 로그인 시 호출되는 인터페이스를 구현

    private final MemberRepository memberRepository; //회원정보를 DB에서 꺼내오거나 저장할 때 MemberRepository를 사용하고, 한 번 주입되면 더 이상 변경되지 않게 유지

    //회원을 저장하는 메서드
    public Member saveMember(Member member) {
        //이메일 중복 체크
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }
    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail()); //DB에서 이메일로 회원 검색
        if (findMember != null) { //이미 가입된 회원이라면 예외를 던져 중복 등록 방지
            throw new IllegalArgumentException("이미 가입된 회원 입니다."); //IllegalArgumentException 예외 발생 -> 컨트롤러에서 처리 가능
        }
    }

    //로그인 시도 시 실행되는 곳 - 로그인 시도 시, 스프링 시큐리티가 자동으로 이 메서드를 호출한다
    @Override
    //로그인할 때 전달된 이메일 기준으로 DB에서 회원을 조회한다
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //로그인 요청 시 loadUserByUsername() 호출

        log.info("----------loadUserByUsername----------");

        Member member = memberRepository.findByEmail(email); //이메일을 기준으로 Member 엔티티를 찾음

        if (member == null){ //회원이 DB에 없을 경우 예외 발생
           throw new UsernameNotFoundException(email); //시큐리티 내부에서 이 예뢰를 인식하여 아이디 없음으로 처리
        }
        return User.builder() //User 스프링 시큐리티에서 제공하는 인증용 객체
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}

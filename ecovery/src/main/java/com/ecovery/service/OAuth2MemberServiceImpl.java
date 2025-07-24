package com.ecovery.service;

import com.ecovery.constant.AgreeType;
import com.ecovery.constant.Role;
import com.ecovery.domain.MemberVO;
import com.ecovery.mapper.MemberMapper;
import com.ecovery.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/*
 * 소셜 로그인(OAuth2) 사용자 처리 서비스 구현체
 * - OAuth2 로그인 요청 시 사용자 정보 추출 및 신규/기존 회원 처리
 * - providerId + provider 값을 기준으로 회원 존재 여부 확인 및 자동 회원가입
 * - CustomUserDetails 객체로 변환 후 Spring Security 인증 객체로 반환
 *
 * @author : yukyeong
 * @fileName : OAuth2MemberServiceImpl
 * @since : 250724
 * @history
 *     - 250724 | yukyeong | 소셜 로그인 사용자 정보 처리 로직 최초 작성
 *     - 250724 | yukyeong | CustomUserDetails에 attributes 주입 방식으로 수정
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2MemberServiceImpl extends DefaultOAuth2UserService
        implements OAuth2MemberService{

    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // access token을 이용해 소셜 사용자 정보(OAuth2User)를 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 소셜 로그인인지 (kakao, google 등)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 받은 사용자 정보 전체 (JSON 구조)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 소셜 제공자 별로 사용자 정보 파싱 (일단은 카카오 전용)
        String providerId;
        String email = null;
        String nickname = null;

        if("kakao".equals(provider)){
            // 카카오 응답에서 세부 정보 추출
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

            providerId = String.valueOf(attributes.get("id")); // 카카오 고유 사용자 ID
            email = (String) kakaoAccount.get("email"); // 사용자 이메일
            nickname = (String) kakaoAccount.get("nickname"); // 사용자 닉네임
        } else {
            // 카카오 외에 다른 로그인 방식이 들어온다면 예외처리
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        }

        // DB에서 해당 provider + providerId로 회원 조회
        MemberVO member = memberMapper.findBySocialId(provider, providerId);

        // DB에서 회원이 없으면 신규 회원으로 등록
        if (member == null){
            member = MemberVO.builder()
                    .email(email)
                    .password(null) // 소셜 로그인은 비밀번호 저장 안함
                    .nickname(nickname)
                    .role(Role.USER) // 기본 User 권한
                    .createdAt(new Date())
                    .agreeOptional(AgreeType.Y) // 기본 약관 동의 처리
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            memberMapper.insertMember(member); // DB에 신규 회원 저장
            log.info("신규 소셜 회원 등록: {}", email);
        } else {
            log.info("기존 소셜 회원 로그인>: {}", email);
        }

        // CustomUserDetails 생성
        CustomUserDetails customUser = new CustomUserDetails(member);

        // 소셜 로그인 사용자 정보(OAuth2 attributes) 주입
        customUser.setAttributes(attributes);

        // 최종 반환
        return customUser;
    }
}

package com.ecovery.service;

import com.ecovery.constant.PayMethod;
import com.ecovery.constant.PayStatus;
import com.ecovery.domain.PaymentVO;
import com.ecovery.dto.PaymentResultDto;
import com.ecovery.mapper.PaymentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/*
 * 에코마켓 결제 ServiceImpl
 * @author : sehui
 * @fileName : PaymentServiceImpl
 * @since : 250725
 * @history
 *  - 250725 | sehui | 결제내역 저장 기능 추가
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final RestTemplate restTemplate;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;

    // 추가
    private final ObjectMapper objectMapper = new ObjectMapper();


    //결제 API Key
    @Value("${portone.imp-key}")
    private String impKey;

    //결제 API Secret
    @Value("${portone.imp-secret}")
    private String impSecret;

    //토큰 발급 메서드
    private String getPortOneAccessToken(){
        log.info("🔑 impKey: {}", impKey);
        log.info("🔐 impSecret: {}", impSecret);

        //토큰 요청할 포트원 공식 API URL
        String url = "https://api.iamport.kr/users/getToken";

        //요청 Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //요청 본문
        Map<String, String> body = new HashMap<>();
        body.put("imp_key", impKey);
        body.put("imp_secret", impSecret);

//          변경 전
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {

            String jsonBody = objectMapper.writeValueAsString(body); // 추가 : JSON 문자열로 변환
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers); // 추가 : JSON 문자열 전달

            //RestTemplate 이용하여 POST 방식으로 포트원에 요청 보냄
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            //요청 성공적 처리 확인
            if(response.getStatusCode() == HttpStatus.OK){
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");

                //응답 본문의 "access_token" 값 반환
                return (String) responseData.get("access_token");
            }else {
                //요청 실패한 경우
                throw new RuntimeException("토큰 발급 실패: "+ response.getBody());
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            //요청 도중에 예외 발생한 경우
            throw  new RuntimeException("토큰 요청 중 오류 발생", e);
        }
    }

    //결제내역 저장
    @Override
    public Long confirmPayment(PaymentResultDto paymentResult, Long memberId) {
        //1. 포트원 서버에서 결제 정보 확인
        String token = getPortOneAccessToken();     //포트원 인증 토큰

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        //결제 정보 조회 URL (imp_uid 사용)
        String url =  "https://api.iamport.kr/payments/" + paymentResult.getPaymentKey();

        //결제 정보 요청
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        if(response.getStatusCode() == HttpStatus.OK){
            //응답에서 결제 관련 정보 추출
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");

            //2. orderId 조회
            Long orderId = orderService.getOrderId(paymentResult.getOrderUuid());
            if(orderId == null){ // 추가 : orderId가 없을 때만 예외 발생
                throw new RuntimeException("주문 정보를 찾을 수 없습니다." + paymentResult.getOrderUuid());
            }

            System.out.println("PaymentResult" + paymentResult);

            //3. 결제 정보 저장용 객체 생성
            PaymentVO payment = PaymentVO.builder()
                    .orderId(orderId)
                    .orderUuid(paymentResult.getOrderUuid())
                    .memberId(memberId)
                    .paymentKey(paymentResult.getPaymentKey())
                    .payMethod(paymentResult.getPayMethod())
                    .payAmount((Integer) responseBody.get("amount"))
                    .payStatus(PayStatus.PAID)
                    .build();

            //4. DB 저장
            paymentMapper.insertPayment(payment);

            return payment.getOrderId();
        }else {
            throw new RuntimeException("결제 정보 확인 실패"+ response.getBody());
        }
    }
}

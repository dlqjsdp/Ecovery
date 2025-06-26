package com.example.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//최초 작성 시간, 수정 시간을 자동으로 기록하기 위핸 베이스 클래스

@EntityListeners(value = {AuditingEntityListener.class}) //JPA가 엔티티를 저장하거나 수정할 때, 시간 관련 이벤트를 감지하고 자동으로 처리하게 함
@MappedSuperclass //DB에 테이블로 생성되지 않지만 상속받은 엔티티가 이 필드를 포함하게 됨
@Getter
@Setter
public class BaseTimeEntity {

    @CreatedDate //엔티티가 처음 저장될 때 자동으로 날짜 입력
    @Column(updatable = false) //최초 작성 일자를 수정하지 말라
    private LocalDateTime regTime; //등록시간(생성일)

    @LastModifiedDate //수정 시간 자동 반영
    private LocalDateTime updateTime; //마지막 수정 시간
}

package com.example.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//작성자와 수정자를 자동으로 채워주는 감시(Auditing)용 엔티티

@EntityListeners(value = {AuditingEntityListener.class}) //감사 기능을 수행하기 위해 이벤트 리스너 등록
@MappedSuperclass //클래스 테이블 아니고 다른 엔티티에서 상속받아 필드만 포함
@Getter
public class BaseEntity extends BaseTimeEntity { //BaseTimeEntity 상속(등록일, 수정일, 등록자, 수정자를 가진 엔티티 상속)

    @CreatedBy //작성자 자동 저장
    @Column(updatable = false)
    private String createdBy; //최초 작성자 정부 추출
 
    @LastModifiedBy //수정자 자동 저장
    private String modifiedBy; //마지막 수정 작성자
}

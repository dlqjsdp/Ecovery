package com.example.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//Auditing 기능을 사용하기 위해 Config 파일 생성

@Configuration //Bean 등록을 위해 기재
@EnableJpaAuditing //자동으로 생성일, 수정일 같은 걸 관리해주는 기능을 활성화
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() { //등록자와 수정자를 처해주는 AuditorAware을 빈으로 등록
        return new AuditorAwareImpl();
    }
}

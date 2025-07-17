package com.ecovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIPredictionResultDto {

    private String predictedClass;  //ai 예측 결과
    //private Double confidence;  //예측 신뢰도
}

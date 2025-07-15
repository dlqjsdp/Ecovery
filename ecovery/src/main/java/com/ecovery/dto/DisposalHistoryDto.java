package com.ecovery.dto;

import lombok.Data;

@Data
public class DisposalHistoryDto {

    private Long disposalHistoryId;
    private Long memberId;
    private String aiPrediction;
    private String regionGu;
    private String createdAt;
    private String finalItem;

    private String itemName;
    private String minPrice;
    private String maxPrice;
    private String reportUrl;

    private String disposalImgUrl;
}

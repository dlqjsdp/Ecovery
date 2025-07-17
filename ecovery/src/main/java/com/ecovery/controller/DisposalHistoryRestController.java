package com.ecovery.controller;

import com.ecovery.service.DisposalHistoryImgService;
import com.ecovery.service.DisposalHistoryService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/disposal")
public class DisposalHistoryRestController {

    private final DisposalHistoryImgService disposalHistoryImgService;
    private final DisposalHistoryService disposalHistoryService;

    @PostMapping("/imgUpload")
    public String imgUpload(@RequestParam("disposalFile") MultipartFile file) {

        return null;
    }

}

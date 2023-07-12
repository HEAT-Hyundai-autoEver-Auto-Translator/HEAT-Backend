package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.language.LanguageDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Transactional
@Slf4j
@RequestMapping("api")
public class LanguageController {

    private final LanguageService languageService;

    @ApiOperation(
            value = "번역 지원 언어 목록 조회 API",
            notes = "현재 HEAT에서 지원하는 번역 언어 리스트를 불러오는 API")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
                description = "결과 전달",
                content = @Content(schema = @Schema(implementation = LanguageDto.class)))})
    @GetMapping("/language")
    ResponseEntity<?> getAvailableLanguageList() {
        List<LanguageDto> availableLanguageDtoList = languageService.getAvailableLanguageList();
        return ResponseEntity.ok(availableLanguageDtoList);
    }
}

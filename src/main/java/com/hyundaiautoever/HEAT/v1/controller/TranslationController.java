package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.user.LoginResponseDto;
import com.hyundaiautoever.HEAT.v1.service.TranslationService;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.Valid;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class TranslationController {

    private final TranslationService translationService;

    @ApiOperation(
            value = "번역 요청 API",
            notes = "번역을 희망하는 텍스트와 희망 언어를 통해 번역을 요청하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class)))})
    @PostMapping("/translation")
    public ResponseEntity<?> requestTranslation(
            @ApiParam(value = "번역 요청 DTO", required = true)
            @RequestBody @Valid RequestTranslationDto requestTranslationDto) {
        Long translationNo = translationService.requestTranslation(requestTranslationDto);
        return ResponseEntity.ok(translationNo);
    }


    @ApiOperation(
            value = "번역 결과 조회 API",
            notes = "번역 테이블의 인덱스로 번역 결과를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class)))})
    @GetMapping("/translation/translation-no") // requestVariable
    public ResponseEntity getTranslationResult(
            @ApiParam(value = "번역 테이블의 인덱스", required = true)
            @RequestParam(value = "translation-no") Long translationNo) {
        TranslationDto translationDto = translationService.getTranslationResult(translationNo);
        return ResponseEntity.ok(translationDto);
    }


    @ApiOperation(
            value = "유저 번역 이력 조회 API",
            notes = "유저의 이메일을 바탕으로 번역 이력을 불러오는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class)))})
    @GetMapping("/translation/user-email")
    public ResponseEntity<?> findTranslationByUserEmail(
            @ApiParam(value = "유저의 이메일", required = true)
            @RequestParam(value = "user-email") String userId) {
        List<TranslationDto> translationDtoList = translationService.findTranslationByUserEmail(userId);
        return ResponseEntity.ok(translationDtoList);
    }


    @ApiOperation(
            value = "전체 번역 기록 조회 API",
            notes = "현재 시스템에서 유지 중인 모든 번역 기록을 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class)))})
    @GetMapping("/translation/history")
    public ResponseEntity<?> findAllTranslation() {
        List<TranslationDto> translationDtoList = translationService.findAllTranslation();
        return ResponseEntity.ok(translationDtoList);
    }


    @ApiOperation(
            value = "번역 이력 삭제 API",
            notes = "번역 테이블의 인덱스로 번역 이력을 삭제하는 API")
    @DeleteMapping("/translation/translation-no")
    public ResponseEntity<?> deleteTranslationRecord(
            @ApiParam(value = "번역 테이블의 인덱스", required = true)
            @RequestParam(value = "translation-no") Long translationNo) {
        translationService.deleteTranslation(translationNo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

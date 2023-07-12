package com.hyundaiautoever.HEAT.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyundaiautoever.HEAT.v1.dto.language.LanguageDto;
import com.hyundaiautoever.HEAT.v1.dto.user.*;
import com.hyundaiautoever.HEAT.v1.service.LoginService;
import com.hyundaiautoever.HEAT.v1.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    @ApiOperation(
            value = "유저 로그인 API",
            notes = "자체 구현 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))})
    @PostMapping("/user/login")
    public ResponseEntity<?> login(
            @ApiParam(value = "로그인 Dto", required = true)
            @RequestBody LoginDto loginDto) throws AuthenticationException {
        LoginResponseDto loginResponseDto = loginService.login(loginDto);
        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + loginResponseDto.getAccessToken())
                .header("Set-Cookie", "refreshToken=" + loginResponseDto.getRefreshToken())
                .body(loginResponseDto);
    }


    @ApiOperation(
            value = "구글 로그인 API",
            notes = "구글 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))})
    @PostMapping("/user/login/google")
    public ResponseEntity<?> googleLogin(
            @ApiParam(value = "구글 API 액세스 토큰", required = true)
            @RequestBody HashMap<String, String> accessToken) throws IOException {
        LoginResponseDto loginResponseDto = loginService.googleLogin(accessToken.get("accessToken"));
        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + loginResponseDto.getAccessToken())
                .header("Set-Cookie", "refreshToken=" + loginResponseDto.getRefreshToken())
                .body(loginResponseDto);
    }


    @ApiOperation(
            value = "리프레시 토큰 검증 API",
            notes = "리프레시 토큰을 통해 유효성을 검사하고 액세스 토큰을 재발급하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))})
    @PostMapping("/user/refresh-token")
    public ResponseEntity<?> verifyRefreshToken(
            @ApiParam(value = "리프레시 토큰", required = true)
            @RequestBody HashMap<String, String> refreshToken) {
        LoginResponseDto loginResponseDto = loginService.getAccessTokenWithRefreshToken(refreshToken.get("refreshToken"));
        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + loginResponseDto.getAccessToken())
                .header("Set-Cookie", "refreshToken=" + loginResponseDto.getRefreshToken())
                .body(loginResponseDto);
    }


    @ApiOperation(
            value = "전체 유저 목록 조회 API",
            notes = "현재 시스템에서 유지 중인 모든 유저 목록을 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))})
    @GetMapping("/user")
    public ResponseEntity<?> getUserList() {
        return ResponseEntity.ok(userService.findAllUser());
    }


    @ApiOperation(
            value = "특정 유저 정보 조회 API",
            notes = "유저 테이블의 인덱스로 유저 정보를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))})
    @GetMapping("/user/uid")
    public ResponseEntity<?> getUserInformation(
            @ApiParam(value = "유저 테이블 인덱스", required = true)
            @RequestParam(value = "uid") Long userAccountNo) {
        return ResponseEntity.ok(userService.findUserInformation(userAccountNo));
    }


    @ApiOperation(
            value = "유저 검색 API",
            notes = "유저의 username을 기반으로 특정 유저를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))})
    @GetMapping("/user/list")
    public ResponseEntity<?> searchUserByUserName(
            @ApiParam(value = "유저 이름", required = true)
            @RequestParam(value = "username") String userName) {
        return ResponseEntity.ok(userService.searchUserByUserName(userName));
    }


    @ApiOperation(
            value = "유저 생성 API",
            notes = "회원가입 시 새로운 유저를 생성하여 DB에 저장하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))})
    @PostMapping("/user")
    public ResponseEntity<?> createUser(
            @ApiParam(value = "유저 생성 Dto", required = true)
            @RequestPart String createUserDto,
            @ApiParam(value = "유저 프로필 이미지", required = true)
            @RequestPart Optional<MultipartFile> userProfileImage) throws IOException {
        CreateUserDto createUserDtoMapped = new ObjectMapper().readValue(createUserDto, CreateUserDto.class);
        if (userProfileImage.isPresent() && !userProfileImage.get().isEmpty()) {
            log.info("이미지 업로드 확인");
        }
        return ResponseEntity.ok(userService.createUser(createUserDtoMapped, userProfileImage));
    }


    @ApiOperation(
            value = "유저 정보 수정 API",
            notes = "유저 정보 변경 시 수정된 정보를 DB에 저장하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))})
    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(
            @ApiParam(value = "유저 정보 수정 Dto", required = true)
            @RequestPart String updateUserDto,
            @ApiParam(value = "유저 프로필 이미지", required = true)
            @RequestPart Optional<MultipartFile> userProfileImage) throws IOException {
        UpdateUserDto updateUserDtoMapped = new ObjectMapper().readValue(updateUserDto, UpdateUserDto.class);
        return ResponseEntity.ok(userService.updateUser(updateUserDtoMapped, userProfileImage));
    }


    @ApiOperation(
            value = "유저 삭제 API",
            notes = "유저 테이블 인덱스로 유저를 삭제하는 API")
    @DeleteMapping("/admin/user")
    public ResponseEntity<?> deleteUser(
            @ApiParam(value = "유저 테이블 인덱스", required = true)
            @RequestParam(value = "uid") Long userAccountNo) {
        userService.deleteUser(userAccountNo);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(
            value = "유저 권한 변경 API",
            notes = "유저의 권한을 변경하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결과 전달",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))})
    @PatchMapping("/admin/user")
    public ResponseEntity<?> updateUser(
            @ApiParam(value = "유저 권한 수정 Dto", required = true)
            @RequestBody AdminUpdateUserDto adminUpdateUserDto) {
        return ResponseEntity.ok(userService.updateUserRole(adminUpdateUserDto));
    }
}

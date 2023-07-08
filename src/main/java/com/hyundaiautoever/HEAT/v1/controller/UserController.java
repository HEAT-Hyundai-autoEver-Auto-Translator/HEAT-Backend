package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.user.*;
import com.hyundaiautoever.HEAT.v1.exception.UserAlreadyExistException;
import com.hyundaiautoever.HEAT.v1.service.LoginService;
import com.hyundaiautoever.HEAT.v1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        LoginResponseDto loginResponseDto = loginService.login(loginDto);
        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + loginResponseDto.getAccessToken())
                .header("Set-Cookie", "refreshToken=" + loginResponseDto.getRefreshToken())
                .body(loginResponseDto);
    }

    @PostMapping("/user/login/google")
    public ResponseEntity<?> googleLogin(@RequestBody HashMap<String, String> accessToken) throws IOException {
        LoginResponseDto loginResponseDto = loginService.googleLogin(accessToken.get("accessToken"));
        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + loginResponseDto.getAccessToken())
                .header("Set-Cookie", "refreshToken=" + loginResponseDto.getRefreshToken())
                .body(loginResponseDto);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserList() {
        return ResponseEntity.ok(userService.findAllUser());
    }

    @GetMapping("/user/uid")
    public ResponseEntity<?> getUserInformation(@RequestParam(value = "uid") Long userAccountNo) {
        return ResponseEntity.ok(userService.findUserInformation(userAccountNo));
    }

    @GetMapping("/user/list")
    public ResponseEntity<?> searchUserByUserName(@RequestParam(value = "username") String userName) {
        return ResponseEntity.ok(userService.searchUserByUserName(userName));
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(
            @RequestPart CreateUserDto createUserDto,
            @RequestPart Optional<MultipartFile> userProfileImage) throws UserAlreadyExistException, IOException {
        if (userProfileImage.isPresent() && !userProfileImage.get().isEmpty()) {
            log.info("이미지 업로드 확인");
        }
        return ResponseEntity.ok(userService.createUser(createUserDto, userProfileImage));
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestPart UpdateUserDto updateUserDto,
            @RequestPart Optional<MultipartFile> userProfileImage) throws IOException {
        return ResponseEntity.ok(userService.updateUser(updateUserDto, userProfileImage));
    }

    @DeleteMapping("/admin/user")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "uid") Long userAccountNo) {
        userService.deleteUser(userAccountNo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/admin/user")
    public ResponseEntity<?> updateUser(@RequestBody AdminUpdateUserDto adminUpdateUserDto) {
        return ResponseEntity.ok(userService.updateUserRole(adminUpdateUserDto));
    }


}

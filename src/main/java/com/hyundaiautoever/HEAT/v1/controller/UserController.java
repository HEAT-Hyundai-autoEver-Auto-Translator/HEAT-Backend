package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.user.LoginDto;
import com.hyundaiautoever.HEAT.v1.dto.user.CreateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UpdateUserDto;
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
        return ResponseEntity.ok(loginService.login(loginDto));
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
            @RequestPart @Valid CreateUserDto createUserDto,
            @RequestPart Optional<MultipartFile> userProfileImage) throws UserAlreadyExistException, IOException {
        if (userProfileImage != null) {
            log.info("이미지 업로드 확인");
        }
        return ResponseEntity.ok(userService.createUser(createUserDto, userProfileImage));
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestPart UpdateUserDto updateUserDto,
            @RequestPart Optional<MultipartFile> userProfileImage) throws IOException {
        return ResponseEntity.ok(userService.updateUser(updateUserDto, userProfileImage));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "uid") Long userAccountNo) {
        userService.deleteUser(userAccountNo);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

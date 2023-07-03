package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.user.CreateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UpdateUserDto;
import com.hyundaiautoever.HEAT.v1.exception.UserAlreadyExistException;
import com.hyundaiautoever.HEAT.v1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserDto createUserDto) throws UserAlreadyExistException {
        return ResponseEntity.ok(userService.createUser(createUserDto));
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(updateUserDto));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "uid") Long userAccountNo) {
        userService.deleteUser(userAccountNo);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

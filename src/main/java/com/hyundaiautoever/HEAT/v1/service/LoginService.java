package com.hyundaiautoever.HEAT.v1.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;
import com.hyundaiautoever.HEAT.v1.dto.user.GoogleResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.user.LoginDto;
import com.hyundaiautoever.HEAT.v1.dto.user.LoginResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.JwtUtils;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {

    //    private final long ACCESS_TOKEN_DURATION = Duration.ofMinutes(30).toMillis();
//    private final long REFRESH_TOKEN_DURATION = Duration.ofDays(14).toMillis();
    private final long ACCESS_TOKEN_DURATION = Duration.ofMinutes(5).toMillis();
    private final long REFRESH_TOKEN_DURATION = Duration.ofMinutes(10).toMillis();

    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleService googleService;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public LoginResponseDto login(LoginDto loginDto) {

        log.info(loginDto.getUserEmail() + loginDto.getUserPassword());
        User user = userRepository.findByUserEmail(loginDto.getUserEmail())
                .filter(m -> passwordEncoder.matches(loginDto.getUserPassword(), m.getPasswordHash()))
                .orElse(null);

        return getLoginResponseDto(user);
    }



    public LoginResponseDto googleLogin (String googleAccessToken) throws IOException {
        //1. 구글에 유저 정보 받아오기
        GoogleResponseDto googleUserInfo = googleService.getUserInfo(googleAccessToken);
        //2. 메일 정보로 현재 등록된 유저인지 확인
        Optional<User> user = userRepository.findByUserEmail(googleUserInfo.getEmail());

        if (user.isEmpty()) {
            //3. 새로운 유저일 경우 DB에 저장
            User newUser = new User();
            newUser.setUserEmail(googleUserInfo.getEmail());
            newUser.setUserName(googleUserInfo.getName());
            newUser.setUserRole(UserRole.user);
            newUser.setProfileImageUrl(googleUserInfo.getPicture());
            //구글 인포 언어 결과 값 확인하기
            Language language = languageRepository.findByLanguageCode(googleUserInfo.getLocale());
            if (language == null) {
                newUser.setLanguage(languageRepository.findByLanguageNo(1));
            } else {
                newUser.setLanguage(language);
            }
            newUser.setSignupDate(LocalDate.now());
            newUser.setLastAccessDate(LocalDate.now());
            User savedUser = userRepository.save(newUser);
            return getLoginResponseDto(savedUser);
        }
        return getLoginResponseDto(user.get());
    }

    public LoginResponseDto getAccessTokenWithRefreshToken(String refreshToken) {
        User user = jwtUtils.validateRefreshToken(refreshToken);
        if (user == null) {
            throw new JwtException("유효한 토큰이 아닙니다.");
        }
        return getLoginResponseDto(user);
    }

    private LoginResponseDto getLoginResponseDto(User user) {
        //유저에 대한 토큰 발급 후 리턴
        //토큰 발급 및 refresh 토큰 저장
        String newAccessToken = jwtUtils.makeJwToken(user, ACCESS_TOKEN_DURATION);
        String newRefreshToken = jwtUtils.makeJwToken(user, REFRESH_TOKEN_DURATION);
        user.setRefreshToken(newRefreshToken);

        //lastAccess Date 수정
        user.setLastAccessDate(LocalDate.now());

        // loginResponseDto 생성
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userAccountNo(user.getUserAccountNo())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return loginResponseDto;
    }
}

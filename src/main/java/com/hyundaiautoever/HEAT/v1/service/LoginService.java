package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.user.GoogleResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.user.LoginDto;
import com.hyundaiautoever.HEAT.v1.dto.user.LoginResponseDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.GoogleUtil;
import com.hyundaiautoever.HEAT.v1.util.JwtUtil;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final long ACCESS_TOKEN_DURATION = Duration.ofMinutes(30).toMillis();
    private final long REFRESH_TOKEN_DURATION = Duration.ofDays(14).toMillis();

    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleUtil googleUtil;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    /**
     * 유저 이메일과 패스워드를 기반으로 DB에 있는 유저를 인증한다.
     *
     * @param loginDto
     * @return 해당 유저의 UserDto 객체 반환
     * @throws AuthenticationException 로그인 실패 시 AuthenticationException 처리
     **/
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginDto loginDto) throws AuthenticationException {

        User user = userRepository.findByUserEmail(loginDto.getUserEmail())
                .filter(m -> passwordEncoder.matches(loginDto.getUserPassword(), m.getPasswordHash()))
                .orElseThrow(() -> new AuthenticationException("로그인에 실패했습니다."));
        return getLoginResponseDto(user);
    }


    /**
     * 구글 액세스 토큰으로 로그인을 지원한다.
     * 구글 로그인을 통해 처음 서비스에 접근한 유저의 경우 구글 API를 통해 유저의 정보를 받아 DB에 저장한다.
     * 구글 API 액세스 토큰을 받아오는 로직은 프론트에서 처리
     *
     * @param googleAccessToken
     * @return 해당 유저의 UserDto 객체 반환
     **/
    @Transactional
    public LoginResponseDto googleLogin(String googleAccessToken) throws IOException {
        //구글에서 유저 정보 받아오기
        GoogleResponseDto googleUserInfo = googleUtil.getUserInfo(googleAccessToken);
        //메일 정보로 현재 등록된 유저인지 확인
        Optional<User> optionalUser = userRepository.findByUserEmail(googleUserInfo.getEmail());

        //구글 지역 기반 언어 설정하기 (기본값 : 영어-LanguageNo.2)
        Language language = Optional
                .ofNullable(languageRepository.findByLanguageCode(googleUserInfo.getLocale()))
                .orElseGet(() -> languageRepository.findByLanguageNo(2));

        //새로운 유저일 경우 DB에 저장
        User user = optionalUser.orElseGet(() -> {
            User newUser = User.builder()
                    .userEmail(googleUserInfo.getEmail())
                    .userName(googleUserInfo.getName())
                    .userRole(UserRole.user)
                    .profileImageUrl(googleUserInfo.getPicture())
                    .language(language)
                    .signupDate(LocalDate.now())
                    .lastAccessDate(LocalDate.now())
                    .build();
            return userRepository.save(newUser);
        });
        return getLoginResponseDto(user);
    }


    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰을 발급한다.
     *
     * @param refreshToken
     * @return LoginResponseDto
     * @throws JwtException 리프레시 토큰이 유효하지 않을 경우
     **/
    public LoginResponseDto getAccessTokenWithRefreshToken(String refreshToken) {
        User user = jwtUtil.validateRefreshToken(refreshToken);
        if (user == null) {
            throw new JwtException("유효한 토큰이 아닙니다.");
        }
        return getLoginResponseDto(user);
    }


    /**
     * 유저 엔티티를 기반으로 LoginResponseDto를 생성한다.
     * 로그인시 마다 액세스 토큰과 리프레시 토큰을 최신화 한다.
     * 유저의 마지막 로그인 일자를 최신화한다.
     *
     * @param user
     * @return 해당 유저의 LoginResponseDto 객체 반환
     * @throws AuthenticationException 로그인 실패 시 AuthenticationException 처리
     **/
    private LoginResponseDto getLoginResponseDto(User user) {
        // 토큰 발급 및 refresh 토큰 저장
        String newAccessToken = jwtUtil.makeJwToken(user, ACCESS_TOKEN_DURATION);
        String newRefreshToken = jwtUtil.makeJwToken(user, REFRESH_TOKEN_DURATION);

        // 유저의 리프레시 토큰 및 마지막 로그인 일자 업데이트
        user.updateLogin(newRefreshToken, LocalDate.now());

        // loginResponseDto 생성
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userAccountNo(user.getUserAccountNo())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return loginResponseDto;
    }
}

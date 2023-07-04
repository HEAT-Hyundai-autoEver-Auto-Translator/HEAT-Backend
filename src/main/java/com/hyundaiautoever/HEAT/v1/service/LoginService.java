package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.user.LoginDto;
import com.hyundaiautoever.HEAT.v1.dto.user.LoginResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public LoginResponseDto login(LoginDto loginDto) {

        LoginResponseDto loginResponseDto = new LoginResponseDto();

        log.info(loginDto.getUserEmail() + loginDto.getUserPassword());
        User user = userRepository.findByUserEmail(loginDto.getUserEmail())
                .filter(m -> passwordEncoder.matches(loginDto.getUserPassword(), m.getPasswordHash()))
                .orElse(null);

        // access 토큰, refresh 토큰 생성
        loginResponseDto.setAccessToken("sdf");
        loginResponseDto.setRefreshToken("fds");

        return loginResponseDto;
    }
}

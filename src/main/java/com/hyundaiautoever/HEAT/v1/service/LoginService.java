package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.LoginDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.mapstruct.factory.Mappers.getMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public UserDto login(LoginDto loginDto) {

        log.info(loginDto.getUserEmail() + loginDto.getUserPassword());
        User user = userRepository.findByUserEmail(loginDto.getUserEmail())
                .filter(m -> passwordEncoder.matches(loginDto.getUserPassword(), m.getPasswordHash()))
                .orElse(null);
        return userMapper.toUserDto(user);
    }
}

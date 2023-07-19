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
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GoogleUtil googleUtil;
    @Mock
    private JwtUtil jwtUtil;

    private LoginDto loginDto1;
    private User user1;

    @BeforeEach
    void setUp() {
        loginDto1 = new LoginDto();
                loginDto1.setUserEmail("test@exmaple.com");
                loginDto1.setUserPassword("123456789a");

        user1 = User.builder()
                .userAccountNo(1L)
                .userEmail("test@exmaple.com")
                .passwordHash("123456789a")
                .userName("User1")
                .userRole(UserRole.user)
                .profileImageUrl("old-image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();
    }

    @Test
    void testLogin() throws AuthenticationException {
        // Arrange
        when(userRepository.findByUserEmail(user1.getUserEmail())).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        LoginResponseDto result = loginService.login(loginDto1);

        // Assert
        verify(userRepository, times(1)).findByUserEmail(loginDto1.getUserEmail());
    }

    @Test
    void testGoogleLogin() throws IOException {
        // Arrange
        String googleAccessToken = "fakeAccessToken";
        when(googleUtil.getUserInfo(googleAccessToken)).thenReturn(GoogleResponseDto.builder()
                .email("test@example.com")
                .name("test")
                .picture("image-url")
                .locale("ru")
                .build());
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user1);

        // Act
        LoginResponseDto result = loginService.googleLogin(googleAccessToken);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGetAccessTokenWithRefreshToken() {
        //Arrange
        String refreshToken = "fakeAccessToken";
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(user1);

        //Act
        LoginResponseDto result = loginService.getAccessTokenWithRefreshToken(refreshToken);

        //Assert
        assertNotNull(result);
    }
}
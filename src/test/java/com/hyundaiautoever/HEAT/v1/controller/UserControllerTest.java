package com.hyundaiautoever.HEAT.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyundaiautoever.HEAT.v1.dto.user.*;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.service.LoginService;
import com.hyundaiautoever.HEAT.v1.service.UserService;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private LoginDto loginDto;

    private LoginResponseDto loginResponseDto;

    private User user;

    private CreateUserDto createUserDto;

    private AdminUpdateUserDto adminUpdateUserDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        loginDto = new LoginDto();
        loginDto.setUserEmail("test@example.com");
        loginDto.setUserPassword("1234567891");

        adminUpdateUserDto = new AdminUpdateUserDto();
                adminUpdateUserDto.setUserAccountNo(1L);
                adminUpdateUserDto.setUserRole("admin");

        loginResponseDto = LoginResponseDto.builder()
                .userAccountNo(1L)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        user = User.builder()
                .userAccountNo(1L)
                .userEmail("user1@example.com")
                .passwordHash("password1")
                .userName("User1")
                .userRole(UserRole.user)
                .profileImageUrl("old-image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        createUserDto = CreateUserDto.builder()
                .userEmail("test@example.com")
                .password("password")
                .userName("userName")
                .languageName("English")
                .build();
    }

    @DisplayName("login 메소드 테스트")
    @Test
    public void testLogin() throws Exception {
        // Arrange
        when(loginService.login(loginDto)).thenReturn(loginResponseDto);

        // Act and Assert
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("googleLogin 메소드 테스트")
    @Test
    public void testGoogleLogin() throws Exception {
        //Arrange
        HashMap<String, String> accessToken = new HashMap<>();
        accessToken.put("accessToken", "test");
        String content = objectMapper.writeValueAsString(accessToken);
        when(loginService.googleLogin(anyString())).thenReturn(loginResponseDto);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/login/google")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("verifyRefreeshToken 메소드 테스트")
    @Test
    public void testVerifyRefreshToken() throws Exception {
        // Arrange
        String testRefreshToken = "testRefreshToken";
        Map<String, String> refreshToken = new HashMap<>();
        refreshToken.put("refreshToken", testRefreshToken);
        when(loginService.getAccessTokenWithRefreshToken(testRefreshToken)).thenReturn(loginResponseDto);

        // Act and Assert
        mockMvc.perform(post("/api/user/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshToken)))
                .andExpect(status().isOk());
    }

    @DisplayName("getUserList 메소드 테스트")
    @Test
    public void testGetUserList() throws Exception {
        // Arrange
        when(userService.findAllUser()).thenReturn(new ArrayList<>());

        // Act and Assert
        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("getUserInformation 메소드 테스트")
    @Test
    public void testGetUserInformation() throws Exception {
        // Arrange
        Long uid = 1L;
        UserDto userDto = new UserDto(user); // assume valid UserDto instance
        Mockito.when(userService.findUserInformation(uid)).thenReturn(userDto);
        // Act and Assert
        mockMvc.perform(get("/api/user/uid")
                        .param("uid", uid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("searchUserByUserName 메소드 테스트")
    @Test
    public void testSearchUserByUserName() throws Exception {
        //Assert
        String userName = "testUser";
        List<UserDto> userDtoList = new ArrayList<>(); // assume valid UserDto list
        Mockito.when(userService.searchUserByUserName(userName)).thenReturn(userDtoList);

        //Act and Arrange
        mockMvc.perform(get("/api/user/list")
                        .param("username", userName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("deleteUser 메소드 테스트")
    @Test
    public void testDeleteUser() throws Exception {
        //Arrange
        Long userAccountNo = 1L; // Example user ID, you might need to change this
        doNothing().when(userService).deleteUser(userAccountNo);

        //Act and Assert
        mockMvc.perform(delete("/api/admin/user")
                        .param("uid", userAccountNo.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService, times(1)).deleteUser(userAccountNo);
    }

    @DisplayName("updateUser 메소드 테스트")
    @Test
    public void testUpdateUser() throws Exception {
        // Arrange
        when(userService.updateUserRole(adminUpdateUserDto)).thenReturn(null); // Adjust based on your method return
        // Act and Assert
        mockMvc.perform(patch("/api/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(adminUpdateUserDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService, times(1)).updateUserRole(adminUpdateUserDto);
    }
}

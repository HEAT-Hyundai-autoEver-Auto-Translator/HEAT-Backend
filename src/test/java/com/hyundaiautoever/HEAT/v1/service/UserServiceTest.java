package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.user.AdminUpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.CreateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.S3Util;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import org.apache.commons.codec.language.bm.Lang;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private S3Util s3Util;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User user1;
    private User user1Admin;
    private User user2;
    private Language language;
    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;
    private Optional<MultipartFile> userProfileImage;
    private AdminUpdateUserDto adminUpdateUserDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userAccountNo(1L)
                .userEmail("user1@example.com")
                .passwordHash("password1")
                .userName("User1")
                .userRole(UserRole.user)
                .profileImageUrl("old-image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        user1Admin = User.builder()
                .userAccountNo(1L)
                .userEmail("user1@example.com")
                .passwordHash("password1")
                .userName("User1")
                .userRole(UserRole.admin)
                .profileImageUrl("old-image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        user2 = User.builder()
                .userAccountNo(2L)
                .userEmail("user2@example.com")
                .passwordHash("password2")
                .userName("User2")
                .userRole(UserRole.user)
                .profileImageUrl("old-image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        language = new Language();
        language.setLanguageCode("en");
        language.setLanguageName("English");
        language.setLanguageNo(2);

        createUserDto = new CreateUserDto();
        createUserDto.setUserEmail("test@example.com");
        createUserDto.setPassword("password");
        createUserDto.setUserName("userName");
        createUserDto.setLanguageName("English");

        updateUserDto = new UpdateUserDto();
        updateUserDto.setUserAccountNo(2L);
        updateUserDto.setPassword("updatedPassword");
        updateUserDto.setUserName("updatedUserName");
        updateUserDto.setLanguageName("English");

        adminUpdateUserDto = new AdminUpdateUserDto();
        adminUpdateUserDto.setUserAccountNo(1L);
        adminUpdateUserDto.setUserRole("admin");

        userProfileImage = Optional.of(new MockMultipartFile("userProfileImage", new byte[0]));
    }

    @DisplayName("findAllUser 메소드 테스트")
    @Test
    void testFindAllUser() {
        // Arrange
        List<User> userList = Arrays.asList(user1, user2);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);
        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<UserDto> result = userService.findAllUser();

        // Assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(userDto1, userDto2);
    }

    @DisplayName("findUserInformation 메소드 테스트")
    @Test
    void testFindUserInformation() {
        //Arrange
        UserDto userDto1 = new UserDto(user1);
        when(userRepository.findByUserAccountNo(1L)).thenReturn(Optional.ofNullable(user1));

        //Act
        UserDto result = userService.findUserInformation(1L);

        //Assert
        assertThat(result).isEqualTo(userDto1);
    }

    @DisplayName("searchUserByUserName 메소드 테스트")
    @Test
    void testSearchUserByUserName() {
        //Arrange
        String userName = "te";
        List<User> userList = Arrays.asList(user1, user2);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);
        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        when(userRepository.findUserByUserName(userName)).thenReturn(userList);

        //Act
        List<UserDto> result = userService.searchUserByUserName(userName);

        //Assert
        assertThat(result).isEqualTo(userDtoList);
    }

    @DisplayName("createUser 메소드 테스트")
    @Test
    void testCreateUser() throws IOException {
        //Arrange
        when(userRepository.findByUserEmail(createUserDto.getUserEmail())).thenReturn(Optional.empty());
        when(s3Util.uploadUserProfileImage(userProfileImage)).thenReturn("image-url");
        when(languageRepository.findByLanguageName(createUserDto.getLanguageName())).thenReturn(new Language());
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encoded-password");

        User savedUser = User.builder()
                .userEmail(createUserDto.getUserEmail())
                .passwordHash("encoded-password")
                .userName(createUserDto.getUserName())
                .userRole(UserRole.user)
                .profileImageUrl("image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        UserDto userDto = new UserDto(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto result = userService.createUser(createUserDto, userProfileImage);

        // Assert
        verify(userRepository, times(1)).findByUserEmail(createUserDto.getUserEmail());
        verify(s3Util, times(1)).uploadUserProfileImage(userProfileImage);
        verify(languageRepository, times(1)).findByLanguageName(createUserDto.getLanguageName());
        verify(passwordEncoder, times(1)).encode(createUserDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(result).isEqualTo(userDto);
    }

    @DisplayName("updateUser 메소드 테스트")
    @Test
    void updateUserTest() throws IOException {
        //Arrange
        LocalDate now = LocalDate.now();
        when(userRepository.findByUserAccountNo(updateUserDto.getUserAccountNo())).thenReturn(Optional.of(user2));
        when(s3Util.uploadUserProfileImage(userProfileImage)).thenReturn("new-image-url");
        when(languageRepository.findByLanguageName(updateUserDto.getLanguageName())).thenReturn(language);

        User updatedUser = User.builder()
                .userAccountNo(2L)
                .userEmail("user2@example.com")
                .passwordHash("updatedPassword")
                .userName("updatedUserName")
                .userRole(UserRole.user)
                .profileImageUrl("new-image-url")
                .language(language)
                .signupDate(now)
                .build();
        UserDto userDto = new UserDto(updatedUser);

        // Act
        UserDto result = userService.updateUser(updateUserDto, userProfileImage);

        // Assert
        verify(userRepository, times(1)).findByUserAccountNo(updateUserDto.getUserAccountNo());
        verify(s3Util, times(1)).uploadUserProfileImage(userProfileImage);
        verify(s3Util, times(1)).removeS3File("old-image-url");
        verify(languageRepository, times(1)).findByLanguageName(updateUserDto.getLanguageName());
        assertThat(result).isEqualTo(userDto);
    }

    @DisplayName("updateUserRole 메소드 테스트")
    @Test
    void testUpdateUserRole() {
        //Arrange
        when(userRepository.findByUserAccountNo(adminUpdateUserDto.getUserAccountNo())).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(user1)).thenReturn(user1Admin);
        UserDto userDto = new UserDto(user1Admin);

        //Act
        UserDto result = userService.updateUserRole(adminUpdateUserDto);

        //Assert
        assertThat(result).isEqualTo(userDto);
    }

    @DisplayName("deleteUSer 메소드 테스트")
    @Test
    void deleteUserTest() {
        //Arrange
        when(userRepository.findByUserAccountNo(1L)).thenReturn(Optional.of(user1));

        //Act
        userService.deleteUser(1L);

        //Assert
        verify(s3Util, times(1)).removeS3File(user1.getProfileImageUrl());
        verify(userRepository, times(1)).deleteByUserAccountNo(1L);
    }
}


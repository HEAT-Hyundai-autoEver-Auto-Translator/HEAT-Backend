package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.user.AdminUpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.CreateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.S3Util;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    /**
     * DB 내 모든 유저 목록을 반환하다.
     *
     * @return 기존까지의 모든 유저 정보
     **/
    @Nullable
    @Transactional(readOnly = true)
    public List<UserDto> findAllUser() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    /**
     * 유저의 정보를 반환한다.
     *
     * @param userAccountNo 유저의 어카운트 넘버
     * @return 유저Dto
     */
    @Nullable
    @Transactional(readOnly = true)
    public UserDto findUserInformation(Long userAccountNo) {
        User user = userRepository.findByUserAccountNo(userAccountNo)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다."));
        return userMapper.toUserDto(user);
    }

    /**
     * 입력되는 글자가 포함된 유저 리스트를 반환한다.
     *
     * @param userName 유저 네임
     * @return 유저Dto 리스트
     */
    @Nullable
    @Transactional(readOnly = true)
    public List<UserDto> searchUserByUserName(String userName) {
        List<User> userList = userRepository.findUserByUserName(userName);
        return userMapper.toUserDtoList(userList);
    }

    /**
     * 새 유저를 생성한다.
     *
     * @param createUserDto    유저 생성 정보
     * @param userProfileImage 수정된 유저 프로필 이미지
     * @return DB에 저장 후 반환되는 유저 엔티티의 DTO 변환값
     */
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto, Optional<MultipartFile> userProfileImage)
            throws IOException {

        LocalDate now = LocalDate.now();
        if (userRepository.findByUserEmail(createUserDto.getUserEmail()).isPresent()) {
            throw new EntityExistsException("이미 존재하는 계정입니다.");
        }
        String userProfileImageUrl = s3Util.uploadUserProfileImage(userProfileImage);
        User user = User.builder()
                .userEmail(createUserDto.getUserEmail())
                .passwordHash(passwordEncoder.encode(createUserDto.getPassword()))
                .userName(createUserDto.getUserName())
                .userRole(UserRole.user)
                .profileImageUrl(userProfileImageUrl)
                .language(languageRepository.findByLanguageName(createUserDto.getLanguageName()))
                .signupDate(now)
                .build();
        return (userMapper.toUserDto(userRepository.save(user)));
    }

    /**
     * 유저 정보를 업데이트 한다.
     *
     * @param updateUserDto    유저 생성 정보
     * @param userProfileImage 유저 프로필 이미지
     * @return DB에 저장 후 반환되는 유저 엔티티의 DTO 변환값
     */
    @Transactional
    public UserDto updateUser(UpdateUserDto updateUserDto, Optional<MultipartFile> userProfileImage)
            throws IOException {

        User user = userRepository.findByUserAccountNo(updateUserDto.getUserAccountNo())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다."));
        // 이미지 url 받아오기
        String userProfileImageUrl = s3Util.uploadUserProfileImage(userProfileImage);
        if (StringUtils.hasText(userProfileImageUrl)) {
            s3Util.removeS3File(user.getProfileImageUrl());
        }
        user.updateBuilder()
                .passwordHash(passwordEncoder.encode(updateUserDto.getPassword()))
                .userName(updateUserDto.getUserName())
                .language(languageRepository.findByLanguageName(updateUserDto.getLanguageName()))
                .profileImageUrl(userProfileImageUrl)
                .build();
        return (userMapper.toUserDto(user));
    }

    /**
     * 유저를 권한을 수정한다.
     *
     * @param adminUpdateUserDto 수정하고자 하는 유저의 accountNo와 새로운 권한 값
     * @return DB에 저장된 user를 변환한 userDto
     */
    @Transactional
    public UserDto updateUserRole(AdminUpdateUserDto adminUpdateUserDto) {
        User user = userRepository.findByUserAccountNo(adminUpdateUserDto.getUserAccountNo())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다."));
        if ("user".equals(adminUpdateUserDto.getUserRole())) {
            user.updateUserRole(UserRole.user);
        } else {
            user.updateUserRole(UserRole.admin);
        }
        userRepository.save(user);
        return (userMapper.toUserDto(user));
    }

    /**
     * 유저를 삭제한다.
     *
     * @param userAccountNo 삭제하고자 하는 유저의 accountNo
     */
    @Transactional
    public void deleteUser(Long userAccountNo) {
        User user = userRepository.findByUserAccountNo(userAccountNo)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다."));
        if (user.getProfileImageUrl() != null) {
            s3Util.removeS3File(user.getProfileImageUrl());
        }
        userRepository.deleteByUserAccountNo(userAccountNo);
    }
}

package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.user.AdminUpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.CreateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.exception.UserAlreadyExistException;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    /**
     * DB 내 모든 유저 목록을 반환하다.
     *
     * @return 기존까지의 모든 유저 정보
     **/
    @Nullable
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
    public UserDto findUserInformation(Long userAccountNo) {
        return userMapper.toUserDto(userRepository.findByUserAccountNo(userAccountNo));
    }


    /**
     * 입력되는 글자가 포함된 유저 리스트를 반환한다.
     *
     * @param userName 유저 네임
     * @return 유저Dto 리스트
     */
    @Nullable
    public List<UserDto> searchUserByUserName(String userName) {
        return userMapper.toUserDtoList(userRepository.findUserByUserName(userName));
    }


    /**
     * 새 유저를 생성한다.
     *
     * @param createUserDto    유저 생성 정보
     * @param userProfileImage 수정된 유저 프로필 이미지
     * @return DB에 저장 후 반환되는 유저 엔티티의 DTO 변환값
     */
    public UserDto createUser(CreateUserDto createUserDto, Optional<MultipartFile> userProfileImage)
            throws UserAlreadyExistException, IOException {

        if (!userRepository.findByUserEmail(createUserDto.getUserEmail()).isEmpty()) {
            throw new UserAlreadyExistException("해당 이메일로 가입한 유저가 이미 존재합니다.");
        }
        User user = new User();
        //유저 이메일 세팅
        user.setUserEmail(createUserDto.getUserEmail());
        //유저 비밀번호 세팅
        user.setPasswordHash(passwordEncoder.encode(createUserDto.getPassword()));
        //유저 네임 세팅
        user.setUserName(createUserDto.getUserName());
        //유저 권한 세팅
        user.setUserRole(UserRole.user);
        //유저 이미지 url 세팅
        String userProfileImageUrl = s3Service.uploadUserProfileImage(userProfileImage.get());
        user.setProfileImageUrl(userProfileImageUrl);
        //유저 언어 세팅
        user.setLanguage(languageRepository.findByLanguageName(createUserDto.getLanguageName()));
        //유저 가입일 세팅
        user.setSignupDate(LocalDate.now());
        return (userMapper.toUserDto(userRepository.save(user)));
    }


    /**
     * 유저 정보를 업데이트 한다.
     *
     * @param updateUserDto    유저 생성 정보
     * @param userProfileImage 유저 프로필 이미지
     * @return DB에 저장 후 반환되는 유저 엔티티의 DTO 변환값
     */
    public UserDto updateUser(UpdateUserDto updateUserDto, Optional<MultipartFile> userProfileImage)
            throws IOException {

        User user = userRepository.findByUserAccountNo(updateUserDto.getUserAccountNo());
        //유저 비밀번호 업데이트
        String newPassword = updateUserDto.getPassword();
        if (passwordValidCheck(newPassword, user.getPasswordHash())) {
            user.setPasswordHash(newPassword);
        }
        //유저 이름 업데이트
        String newUserName = updateUserDto.getUserName();
        if (validCheck(newUserName, user.getUserName())) {
            user.setUserName(newUserName);
        }
        //유저 프로필 사진 업데이트
        if (userProfileImage.isPresent()) {
            //기존 이미지 삭제
            s3Service.removeS3File(user.getProfileImageUrl());
            String userProfileImageUrl = s3Service.uploadUserProfileImage(userProfileImage.get());
            user.setProfileImageUrl(userProfileImageUrl);
        }
        //유저 언어 업데이트
        String newLanguageName = updateUserDto.getLanguageName();
        if (validCheck(newLanguageName, user.getLanguage().getLanguageName())) {
            user.setLanguage(languageRepository.findByLanguageName(newLanguageName));
        }
        return (userMapper.toUserDto(user));
    }

    /**
     * 유저를 권한을 수정한다.
     *
     * @param adminUpdateUserDto 수정하고자 하는 유저의 accountNo와 새로운 권한 값
     * @return DB에 저장된 user를 변환한 userDto
     */
    public UserDto updateUserRole(AdminUpdateUserDto adminUpdateUserDto) {
        User user = userRepository.findByUserAccountNo(adminUpdateUserDto.getUserAccountNo());
        if (adminUpdateUserDto.getUserRole().equals("user")) {
            user.setUserRole(UserRole.user);
        } else {
            user.setUserRole(UserRole.admin);
        }
        user = userRepository.save(user);
        return (userMapper.toUserDto(user));
    }

    /**
     * 유저를 삭제한다.
     *
     * @param userAccountNo 삭제하고자 하는 유저의 accountNo
     */
    public void deleteUser(Long userAccountNo) {
        User user = userRepository.findByUserAccountNo(userAccountNo);
        s3Service.removeS3File(user.getProfileImageUrl());
        userRepository.deleteByUserAccountNo(userAccountNo);
    }


    private boolean validCheck(String newValue, String oldValue) {
        if (newValue != null && !newValue.equals(oldValue) && newValue.length() != 0) {
            return true;
        }
        return false;
    }

    private boolean passwordValidCheck(String newPassword, String oldPasswordHash) {
        //검증조건 이후에 추가하기
        if (newPassword != null && newPassword.length() != 0 && passwordEncoder.matches(newPassword, oldPasswordHash)) {
            return true;
        }
        return false;
    }

}

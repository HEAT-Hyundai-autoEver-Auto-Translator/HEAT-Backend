package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.user.CreateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UpdateUserDto;
import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.exception.UserAlreadyExistException;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.UserMapper;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import com.querydsl.core.dml.UpdateClause;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
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
     * 유저 별 번역 이력을 반환한다.
     *
     * @param userAccountNo 유저의 어카운트 넘버
     * @return 유저Dto
     */
    @Nullable
    public UserDto findUserInformation(Long userAccountNo) {
        return userMapper.toUserDto(userRepository.findByUserAccountNo(userAccountNo));
    }


    /**
     * 유저 별 번역 이력을 반환한다.
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
     * @param createUserDto 유저 생성 정보
     * @return DB에 저장 후 반환되는 유저 엔티티의 DTO 변환값
     */
    public UserDto createUser(CreateUserDto createUserDto) throws UserAlreadyExistException {

        if (userRepository.findByUserEmail(createUserDto.getUserEmail()) != null) {
            throw new UserAlreadyExistException("해당 이메일로 가입한 유저가 이미 존재합니다.");
        }
        User user = new User();
        user.setUserEmail(createUserDto.getUserEmail());
        user.setPasswordHash(passwordEncoder.encode(createUserDto.getPassword()));
        user.setUserName(createUserDto.getUserName());
        user.setUserRole(UserRole.user);
        user.setProfileImageUrl(createUserDto.getProfileImageUrl());
        user.setLanguage(languageRepository.findByLanguageNo(createUserDto.getLanguageNo()));
        user.setSignupDate(LocalDate.now());
        return (userMapper.toUserDto(userRepository.save(user)));
    }


    public UserDto updateUser(UpdateUserDto updateUserDto) {
        User user = userRepository.findByUserAccountNo(updateUserDto.getUserAccountNo());
        String newUserEmail = updateUserDto.getUserEmail();

        if (newUserEmail != null && !user.getUserEmail().equals(newUserEmail)) {
            user.setUserEmail(newUserEmail);
        }

        String newPassword = updateUserDto.getPassword();
        if (newPassword != null && !newPassword.equals(user.getPasswordHash())) {
            user.setPasswordHash(newPassword);
        }

        String newUserName = updateUserDto.getUserName();
        if (newUserName != null && !newUserName.equals(user.getUserName())) {
            user.setUserName(newUserName);
        }

        String newProfileImageUrl = updateUserDto.getProfileImageUrl();
        if (newProfileImageUrl != null && !newProfileImageUrl.equals(user.getProfileImageUrl())) {
            user.setProfileImageUrl(newProfileImageUrl);
        }

        Integer newLanguageNo = updateUserDto.getLanguageNo();
        if (newLanguageNo != null && newLanguageNo != user.getLanguage().getLanguageNo()) {
            user.setLanguage(languageRepository.findByLanguageNo(newLanguageNo));
        }
        return (userMapper.toUserDto(userRepository.save(user)));
    }


    /**
     * 유저를 삭제한다.
     *
     * @param userAccountNo 삭제하고자 하는 유저의 accountNo
     */
    public void deleteUser(Long userAccountNo) {
        userRepository.deleteByUserAccountNo(userAccountNo);
    }

}

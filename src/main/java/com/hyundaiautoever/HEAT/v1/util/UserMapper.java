package com.hyundaiautoever.HEAT.v1.util;

import com.hyundaiautoever.HEAT.v1.dto.user.UserDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    UserDto toUserDto(User user);

    default String toLanguageName(Language language) { return language.getLanguageName();}

    List<UserDto> toUserDtoList(List<User> userList);

}

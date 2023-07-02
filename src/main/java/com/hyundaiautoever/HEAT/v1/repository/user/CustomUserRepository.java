package com.hyundaiautoever.HEAT.v1.repository.user;

import com.hyundaiautoever.HEAT.v1.entity.User;

import java.util.List;

public interface CustomUserRepository {

    List<User> findUserByUserName(String userName);

}

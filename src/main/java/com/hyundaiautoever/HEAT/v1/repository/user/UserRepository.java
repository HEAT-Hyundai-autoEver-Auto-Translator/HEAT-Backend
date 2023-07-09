package com.hyundaiautoever.HEAT.v1.repository.user;

import com.hyundaiautoever.HEAT.v1.entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    User findByUserAccountNo(Long userAccountNo);
    Optional<User> findByUserEmail(String userEmail);
    List<User> findUserByUserName(String userName);
    void deleteByUserAccountNo(Long userAccountNo);
}

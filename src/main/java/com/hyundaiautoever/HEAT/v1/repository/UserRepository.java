package com.hyundaiautoever.HEAT.v1.repository;

import com.hyundaiautoever.HEAT.v1.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserAccountNo(Long userAccountNo);
    User findByUserEmail(String userEmail);
    List<User> findByUserRole(String userRole);
}

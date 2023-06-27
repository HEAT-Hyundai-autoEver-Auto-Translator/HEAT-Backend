package com.hyundaiautoever.HEAT.v1.repository;

import com.hyundaiautoever.HEAT.v1.entitiy.User;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserAccountNo(Long userAccountNo);

    User findByUserId(String userId);

    List<User> findByUserRole(String userRole);

}

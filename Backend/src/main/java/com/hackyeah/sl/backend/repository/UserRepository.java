package com.hackyeah.sl.backend.repository;


import com.hackyeah.sl.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  User findUserByEmail(String email);

  void deleteByEmail(String username);
}

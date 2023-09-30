package com.hackyeah.sl.backend.domain.mappers;


import com.hackyeah.sl.backend.domain.DTO.UserLogin;
import com.hackyeah.sl.backend.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserLoginMapper {
  UserLoginMapper INSTANCE = Mappers.getMapper(UserLoginMapper.class);

  UserLogin userToUserLogin(User user);

  User userLoginToUser(UserLogin userLogin);
}

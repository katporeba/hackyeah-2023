package com.hackyeah.sl.backend.service;


import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
  User register(String email, String password, String repeatPassword)
          throws UserNotFoundException, EmailExistException, UsernameExistException, PasswordsDontMatchException;

  List<User> getUsers();

  //User findUserByUsername(String username);

  User findUserByEmail(String email);

  User addNewUser(
      String email,
      String password,
      String role,
      boolean isNonLocked,
      boolean isActive,
      MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

  User updateUser(
      String userName,
      String newFirstName,
      String newLastName,
      String newUsername,
      String newEmail,
      String role,
      boolean isNonLocked,
      boolean isActive,
      MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

  void deleteUser(String email) throws IOException;

  void resetPassword(String email) throws EmailNotFoundException;

  User updateProfileImage(String username, MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
}

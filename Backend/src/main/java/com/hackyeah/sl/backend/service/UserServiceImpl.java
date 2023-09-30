package com.hackyeah.sl.backend.service;


import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.domain.UserPrincipal;
import com.hackyeah.sl.backend.enumeration.Role;
import com.hackyeah.sl.backend.exception.domain.*;
import com.hackyeah.sl.backend.repository.TicketRepository;
import com.hackyeah.sl.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import static com.hackyeah.sl.backend.constant.FileConstant.*;
import static com.hackyeah.sl.backend.constant.UserImplConstant.*;
import static com.hackyeah.sl.backend.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Slf4j
@Service
@Transactional
@Qualifier("userDetailService")
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final LoginAttemptService loginAttemptService;
  private final TicketRepository ticketRepository;

  @SneakyThrows
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findUserByEmail(email);
    if (user == null) {
      log.error("User not found by email: " + email);
      throw new UserNotFoundException("User not found by email: " + email);
    } else {
      validateLoginAttempt(user);
      user.setLastLoginDateDisplay(user.getLastLoginDate());
      user.setLastLoginDate(new Date());
      userRepository.save(user);
      log.info("Found user by email: " + email);
      return new UserPrincipal(user);
    }
  }

  private void validateLoginAttempt(User user) {
    if (user.isNonLocked()) {
      user.setNonLocked(!loginAttemptService.hasExceededMaxAttempt(user.getEmail()));
    } else {
      loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
    }
  }

  @Override
  public User register(String email, String password, String repeatPassword)
          throws UserNotFoundException, EmailExistException, UsernameExistException, PasswordsDontMatchException {

    validateNewUsernameAndEmail(EMPTY, email);
    validateIfPasswordsAreEqual(password, repeatPassword);
    User user = new User();
    user.setUserId(generateUserId());
    String encodedPassword = encodePassword(password);
    user.setPassword(encodedPassword);
    //user.setFirstName(firstName);
    //user.setUsername(username);
    //user.setLastName(lastName);
    user.setEmail(email);
    user.setJoinDate(new Date());
    user.setActive(true);
    user.setNonLocked(true);
    user.setRole(ROLE_USER.name());
    user.setProfileImageUrl(getDefaultProfileImageUrl(email));

    log.info(user.toString());

    User savedUser = userRepository.save(user);
    log.info("New user password: " + password);
    return savedUser;
  }

  private void validateIfPasswordsAreEqual(String password, String repeatPassword) throws PasswordsDontMatchException {
    if (!password.equals(repeatPassword)) {
      throw new PasswordsDontMatchException("Passwords are not equal");
    }
  }

  private String getDefaultProfileImageUrl(String username) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(DEFAULT_USER_IMAGE_PATH + username)
        .toUriString();
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  private String generatePassword() {
    return RandomStringUtils.randomAlphanumeric(10);
  }

  private String generateUserId() {
    return RandomStringUtils.randomNumeric(10);
  }

  private User validateNewUsernameAndEmail(
      String currentUsername, String newEmail)
      throws UserNotFoundException, UsernameExistException, EmailExistException {
//    User userByNewUsername = findUserByUsername(newUsername);
//    User userByNewEmail = findUserByEmail(newEmail);
//
//    if (StringUtils.isNoneBlank(currentUsername)) {
//      User currentUser = findUserByUsername(currentUsername);
//      if (currentUser == null) {
//        throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + userRepository);
//      }
//      if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
//        throw new UsernameExistException(USERNAME_ALREADY_EXIST + userByNewUsername);
//      }
//
//      if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
//        throw new EmailExistException(EMAIL_ALREADY_TAKEN + userByNewEmail.getEmail());
//      }
//      return currentUser;
//    } else {
//      if (userByNewUsername != null) {
//        throw new UsernameExistException(USERNAME_ALREADY_EXIST + userByNewUsername);
//      }
//      if (userByNewEmail != null) {
//        throw new EmailExistException(EMAIL_ALREADY_TAKEN + userByNewEmail.getEmail());
//      }
      return null;
    }


  @Override
  public List<User> getUsers() {
    return userRepository.findAll();
  }

//  @Override
//  public User findUserByUsername(String username) {
//    return userRepository.findUserByUsername(username);
//  }

  @Override
  public User findUserByEmail(String email) {
    return userRepository.findUserByEmail(email);
  }

  @Override
  public User addNewUser(
      String email,
      String password,
      String role,
      boolean isNonLocked,
      boolean isActive,
      MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {

    validateNewUsernameAndEmail(EMPTY, email);
    User user = new User();
    //
    String encodedPassword = encodePassword(password);
    user.setUserId(generateUserId());
    //user.setUsername(username);
/*    user.setFirstName(firstName);
    user.setLastName(lastName);*/
    user.setJoinDate(new Date());
    user.setPassword(encodedPassword);
    user.setEmail(email);
    user.setNonLocked(isNonLocked);
    user.setActive(isActive);
    user.setRole(getRoleEnumName(role).name());
    //user.setAuthorities(getRoleEnumName(role).getAuthorities());
    user.setProfileImageUrl(getDefaultProfileImageUrl(email));
    User savedUser = userRepository.save(user);
    //saveTicketImage(savedUser, profileImage);

    //emailService.sendNewPasswordEmail(savedUser.getFirstName(), password, email);
    log.info("New user password: " + password);
    return user;
  }

  public void saveTicketImage(Ticket ticket, MultipartFile image)
      throws IOException, NotAnImageFileException {
    if (image != null) {
      if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
          .contains(image.getContentType())) {
        throw new NotAnImageFileException(image.getOriginalFilename() + " is not an image.");
      }
      Path userFolder = Paths.get(USER_FOLDER + ticket.getTicketId()).toAbsolutePath().normalize();
      if (!Files.exists(userFolder)) {
        Files.createDirectories(userFolder);
        log.info(DIRECTORY_CREATED + userFolder);
      }
      Files.deleteIfExists(Paths.get(USER_FOLDER + ticket.getTicketId() + DOT + JPG_EXTENSION));
      Files.copy(
          image.getInputStream(),
          userFolder.resolve(ticket.getTicketId() + DOT + JPG_EXTENSION),
          REPLACE_EXISTING);

      ticket.setTicketImageUrl(setProfileImageUrl(ticket.getTicketId()));

      ticketRepository.save(ticket);

      log.info(FILE_SAVED_IN_FILE_SYSTEM + image.getOriginalFilename());
    }
  }

  private String setProfileImageUrl(String ticketId) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(USER_IMAGE_PATH + ticketId + FORWARD_SLASH + ticketId + DOT + JPG_EXTENSION)
        .toUriString();
  }

  private Role getRoleEnumName(String role) {
    return Role.valueOf(role.toUpperCase());
  }

  @Override
  public User updateUser(
      String userName,
      String newFirstName,
      String newLastName,
      String newUsername,
      String newEmail,
      String role,
      boolean isNonLocked,
      boolean isActive,
      MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {

    User currentUser = validateNewUsernameAndEmail(userName, newEmail);

    //currentUser.setFirstName(newFirstName);
    //currentUser.setLastName(newLastName);
    //currentUser.setUsername(newUsername);
    currentUser.setJoinDate(new Date());
    currentUser.setEmail(newEmail);
    currentUser.setNonLocked(isNonLocked);
    currentUser.setActive(isActive);
    currentUser.setRole(getRoleEnumName(role).name());
    //currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
    User savedUser = userRepository.save(currentUser);
    //saveTicketImage(savedUser, profileImage);
    return currentUser;
  }

  @Override
  public void deleteUser(String email) {
    Path userFolder = Paths.get(USER_FOLDER + email).toAbsolutePath().normalize();
    try {
      FileUtils.deleteDirectory(new File(userFolder.toString()));
    } catch (IOException exception) {
      log.error(exception.getMessage());
      exception.printStackTrace();
    }
    userRepository.deleteByEmail(email);
  }

  @Override
  public void resetPassword(String email) throws EmailNotFoundException {
    User user = userRepository.findUserByEmail(email);
    if (user == null) {
      throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
    }
    String password = generatePassword();
    user.setPassword(encodePassword(password));
    userRepository.save(user);
    //emailService.sendNewPasswordEmail(user.getEmail(), password, user.getEmail());
  }

  @Override
  public User updateProfileImage(String email, MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
    User user = validateNewUsernameAndEmail(email, null);
    //saveTicketImage(user, profileImage);
    return user;
  }
}

package com.hackyeah.sl.backend.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackyeah.sl.backend.domain.DTO.UserRegister;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.exception.domain.EmailExistException;
import com.hackyeah.sl.backend.exception.domain.UsernameExistException;
import com.hackyeah.sl.backend.repository.UserRepository;
import com.hackyeah.sl.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.FileInputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class UserResourceTest {

  private static final String API_ROOT = "/user";
  private static final String[] authorities_user = {"ROLE_USER"};
  public static final String PASSWORD = "somepass";
  public static final String USERNAME = "jdoe";
  public static final String USERNAME_OKOT = "olakot";
  public static final String AVATAR_JPG = "src/test/resources/images/avatar.jpg";

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private UserService userService;

  @Autowired ObjectMapper objectMapper;

  User one, two, three, fourth;

  UserRegister oneRegister, threeRegister;

  @BeforeEach
  void setUp() {
    userRepository.flush();

    log.info("Size: {}", userRepository.findAll().size()) ;

    one =
        User.builder()
            .userId("2131j2asda")
            .email("one@gmail.com")
            .isActive(true)
            .isNonLocked(true)
                .role("ROLE_USER")
            .password(new BCryptPasswordEncoder().encode(PASSWORD))
            .build();

    oneRegister = UserRegister.builder()
            .email("one@gmail.com")
            .password("test1234")
            .repeatPassword("test1234")
            .build();


    two =
        User.builder()
            .userId("213qwe2asda")
            .email("two@gmail.com")
            .role("ROLE_ADMIN")
            .isActive(true)
            .isNonLocked(true)
            .password(new BCryptPasswordEncoder().encode(PASSWORD))
            .build();

    three =
        User.builder()
            .email("three@gmail.com")
            .build();

    threeRegister = UserRegister.builder()
            .email(three.getEmail())
            .password("test1234")
            .repeatPassword("test1234")
            .build();

    fourth =
        User.builder()
            .email("ncage@gmail.com")
            .role("ROLE_USER")
            .isActive(false)
            .isNonLocked(false)
            .build();

    userRepository.saveAll(Arrays.asList(one, two));
    log.info("Size after: {}", userRepository.findAll().size()) ;

  }

  @Test
  void register() throws Exception {


    mockMvc
        .perform(
            post(API_ROOT + "/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(threeRegister)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", Matchers.is(three.getEmail())));
  }

  @Test
  void registerUserThatExist() throws Exception {

    mockMvc
        .perform(
            post(API_ROOT + "/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneRegister)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof EmailExistException));
  }

  @Test
  void registerEmailThatExist() throws Exception {


    threeRegister.setEmail(one.getEmail());

    mockMvc
        .perform(
            post(API_ROOT + "/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(threeRegister)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof EmailExistException));
  }

  @Test
  void addNotAuthenticated() throws Exception {


    mockMvc
        .perform(
            post(API_ROOT + "/add")
                .contentType(MULTIPART_FORM_DATA)
                .param("email", fourth.getEmail())
                .param("isActive", String.valueOf(fourth.isActive()))
                .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                .param("role", fourth.getRole()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:create"})
  void addAuthenticated() throws Exception {

    mockMvc
        .perform(
            post(API_ROOT + "/add")
                .contentType(MULTIPART_FORM_DATA)
                .param("email", "someemail@cos.pl")
                .param("password", "test1234")
                .param("isActive", String.valueOf(true))
                .param("nonLocked", String.valueOf(true))
                .param("role", fourth.getRole()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email", Matchers.is("someemail@cos.pl")));
  }

  @Test
  void updateNotAuthenticated() throws Exception {
    mockMvc
        .perform(
            post(API_ROOT + "/update")
                .contentType(MULTIPART_FORM_DATA)
                .param("email", "newemail@email.com")
                .param("isActive", String.valueOf(two.isActive()))
                .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                .param("role", two.getRole()))
        .andExpect(status().isForbidden());
  }

  @Test
  void loginWrongCredentials() throws Exception {

    JSONObject json = new JSONObject();
    json.put("email", one.getEmail());
    json.put("password", "wrongpassword");

    mockMvc
        .perform(
            post(API_ROOT + "/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json.toJSONString()))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
        .andExpect(jsonPath("$.httpStatus", Matchers.is("BAD_REQUEST")));
  }

  @Test
  void loginCorrectCredentials() throws Exception {

    JSONObject json = new JSONObject();
    json.put("email", one.getEmail());
    json.put("password", PASSWORD);

    mockMvc
        .perform(
            post(API_ROOT + "/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json.toJSONString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", Matchers.is(one.getEmail())));
  }

  @Test
  void loginAccountLocked() throws Exception {

    one.setNonLocked(false);
    userRepository.save(one);

    JSONObject json = new JSONObject();
    json.put("email", one.getEmail());
    json.put("password", PASSWORD);

    mockMvc
        .perform(
            post(API_ROOT + "/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json.toJSONString()))
        .andExpect(status().isUnauthorized())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof LockedException));
  }

  @Test
  void loginAccountNotActive() throws Exception {

    one.setActive(false);
    userRepository.save(one);

    JSONObject json = new JSONObject();
    json.put("email", one.getEmail());
    json.put("password", PASSWORD);

    mockMvc
        .perform(
            post(API_ROOT + "/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json.toJSONString()))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof DisabledException));
  }

  @Test
  void findUserNotAuthenticated() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/find/" + USERNAME))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void findUserAuthenticated() throws Exception {
    mockMvc.perform(get(API_ROOT + "/find/" + USERNAME)).andDo(print()).andExpect(status().isOk());
  }

  @Test
  void userListNotAuthenticated() throws Exception {
    mockMvc.perform(get(API_ROOT + "/list")).andDo(print()).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void userListAuthenticated() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/list"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].email", Matchers.is(one.getEmail())));
  }

  @Test
  void deleteUserNoAuthentication() throws Exception {
    mockMvc
        .perform(delete(API_ROOT + "/delete/" + one.getEmail()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:delete"})
  void deleteUserAdmin() throws Exception {
    mockMvc.perform(delete(API_ROOT + "/delete/" + one.getEmail())).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:delete"})
  void deleteUserMethodNotAllowed() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/delete/" + one.getEmail()))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException()
                        instanceof HttpRequestMethodNotSupportedException));
  }

  @Test
  @WithMockUser(
      username = "manager",
      authorities = {"user:read", "user:update"})
  void deleteUserAnonymousUser() throws Exception {
    mockMvc
        .perform(delete(API_ROOT + "/delete/" + one.getEmail()))
        .andExpect(status().isForbidden())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
  }

  @Test
  void getTempProfileImage() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/image/profile/" + one.getEmail()))
        .andExpect(status().isOk());
  }

}

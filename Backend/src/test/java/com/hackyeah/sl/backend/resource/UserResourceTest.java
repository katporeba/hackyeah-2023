package com.hackyeah.sl.backend.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackyeah.sl.backend.domain.DTO.UserRegister;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.repository.UserRepository;
import com.hackyeah.sl.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@Testcontainers
//@ActiveProfiles("test-containers")
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
/*
  @BeforeEach
  void setUp() {
    userRepository.flush();

    log.info("Size: {}", userRepository.findAll().size()) ;

    one =
        User.builder()
            .userId("2131j2asda")
            .email("one@gmail.com")
            .firstName("Joe")
            .lastName("Doe")
            //.username(USERNAME)
            .isActive(true)
            .isNonLocked(true)
                .role("ROLE_USER")
            //.authorities(authorities_user)
            .password(new BCryptPasswordEncoder().encode(PASSWORD))
            .build();

    oneRegister = UserRegister.builder()
            .email("one@gmail.com")
            //.firstName("Joe")
            .lastName("Doe")
            .username(USERNAME)
            .password("test1234")
            .repeatPassword("test1234")
            .build();


    two =
        User.builder()
            .userId("213qwe2asda")
            .email("two@gmail.com")
            .firstName("Ola")
            .lastName("Kot")
            //.username(USERNAME_OKOT)
            .role("ROLE_ADMIN")
            .isActive(true)
            .isNonLocked(true)
            //.authorities(authorities_user)
            .password(new BCryptPasswordEncoder().encode(PASSWORD))
            .build();

    three =
        User.builder()
            .email("three@gmail.com")
            .firstName("Rick")
            .lastName("Morty")
            //.username("rmorty")
            .build();

    threeRegister = UserRegister.builder()
            .email(three.getEmail())
            //.firstName("Joe")
            //.lastName("Doe")
            //.username(three.getUsername())
            .password("test1234")
            .repeatPassword("test1234")
            .build();

    fourth =
        User.builder()
            .email("ncage@gmail.com")
            .firstName("Nicolas")
            .lastName("Cage")
            //.username("ncage")
            .role("ROLE_USER")
            .isActive(false)
            .isNonLocked(false)
            //.authorities(authorities_user)
            .build();

    userRepository.saveAll(Arrays.asList(one, two));
    log.info("Size after: {}", userRepository.findAll().size()) ;

  }

  @Test
  void register() throws Exception {

    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

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

    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

    mockMvc
        .perform(
            post(API_ROOT + "/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneRegister)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof UsernameExistException));
  }

  @Test
  void registerEmailThatExist() throws Exception {

    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

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

    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

    mockMvc
        .perform(
            post(API_ROOT + "/add")
                .contentType(MULTIPART_FORM_DATA)
                .param("email", fourth.getEmail())
                .param("firstName", fourth.getFirstName())
                .param("isActive", String.valueOf(fourth.isActive()))
                .param("lastName", fourth.getLastName())
                .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                .param("role", fourth.getRole())
                .param("username", fourth.getUsername()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:create"})
  void addAuthenticated() throws Exception {
    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

    mockMvc
        .perform(
            post(API_ROOT + "/add")
                .contentType(MULTIPART_FORM_DATA)
                .param("email", fourth.getEmail())
                .param("password", "test1234")
                .param("firstName", fourth.getFirstName())
                .param("isActive", String.valueOf(fourth.isActive()))
                .param("lastName", fourth.getLastName())
                .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                .param("role", fourth.getRole())
                .param("username", fourth.getUsername()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email", Matchers.is(fourth.getEmail())));
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:update"})
  void updateAuthenticated() throws Exception {
    mockMvc
        .perform(
            post(API_ROOT + "/update")
                .contentType(MULTIPART_FORM_DATA)
                .param("currentUsername", two.getUsername())
                .param("email", "newemail@email.com")
                .param("firstName", two.getFirstName())
                .param("isActive", String.valueOf(two.isActive()))
                .param("lastName", two.getLastName())
                .param("nonLocked", String.valueOf(two.isNonLocked()))
                .param("role", two.getRole())
                .param("username", two.getUsername()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email", Matchers.is("newemail@email.com")));
  }

  @Test
  @Transactional
  @WithMockUser(
      username = "admin",
      authorities = {"user:update"})
  void updateAuthenticatedCurrentUserDontExist() throws Exception {
    mockMvc
        .perform(
            post(API_ROOT + "/update")
                .contentType(MULTIPART_FORM_DATA)
                .param("currentUsername", "usernamethatdontexist")
                .param("email", "newemail@email.com")
                .param("firstName", two.getFirstName())
                .param("isActive", String.valueOf(two.isActive()))
                .param("lastName", two.getLastName())
                .param("nonLocked", String.valueOf(two.isNonLocked()))
                .param("role", two.getRole())
                .param("username", two.getUsername()))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
  }

  @Test
  void updateNotAuthenticated() throws Exception {
    mockMvc
        .perform(
            post(API_ROOT + "/update")
                .contentType(MULTIPART_FORM_DATA)
                .param("currentUsername", two.getUsername())
                .param("email", "newemail@email.com")
                .param("firstName", two.getFirstName())
                .param("isActive", String.valueOf(two.isActive()))
                .param("lastName", two.getLastName())
                .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                .param("role", two.getRole())
                .param("username", two.getUsername()))
        .andExpect(status().isForbidden());
  }

  @Test
  void loginWrongCredentials() throws Exception {

    JSONObject json = new JSONObject();
    json.put("username", one.getUsername());
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
    json.put("username", one.getUsername());
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
    json.put("username", one.getUsername());
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
    json.put("username", one.getUsername());
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
        .andExpect(jsonPath("$.[0].username", Matchers.is(USERNAME)));
  }

  @Test
  void resetPasswordNoAuthentication() throws Exception {
    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

    mockMvc
        .perform(get(API_ROOT + "/resetPassword/" + one.getEmail()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void resetPasswordAuthenticated() throws Exception {
    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

    mockMvc
        .perform(get(API_ROOT + "/resetPassword/" + one.getEmail()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.httpStatus", Matchers.is("OK")));
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void resetPasswordWithNotValidEmail() throws Exception {
    doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

    mockMvc
        .perform(get(API_ROOT + "/resetPassword/" + "notvalidemail"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void resetPasswordAuthenticatedUserNotFound() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/resetPassword/" + "emailthatdontexist@email.com"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof EmailNotFoundException));
  }

  @Test
  void deleteUserNoAuthentication() throws Exception {
    mockMvc
        .perform(delete(API_ROOT + "/delete/" + USERNAME_OKOT))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:delete"})
  void deleteUserAdmin() throws Exception {
    mockMvc.perform(delete(API_ROOT + "/delete/" + USERNAME_OKOT)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:delete"})
  void deleteUserMethodNotAllowe() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/delete/" + USERNAME_OKOT))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException()
                        instanceof HttpRequestMethodNotSupportedException));
  }

  // HttpRequestMethodNotSupportedException

  @Test
  @WithMockUser(
      username = "manager",
      authorities = {"user:read", "user:update"})
  void deleteUserAnonymousUser() throws Exception {
    mockMvc
        .perform(delete(API_ROOT + "/delete/" + USERNAME_OKOT))
        .andExpect(status().isForbidden())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getProfileImage() throws Exception {
    FileInputStream avatar = new FileInputStream(AVATAR_JPG);
    MockMultipartFile multipartFile =
        new MockMultipartFile("profileImage", "avatar.jpg", IMAGE_JPEG_VALUE, avatar);
    userService.updateProfileImage(one.getUsername(), multipartFile);

    mockMvc
        .perform(get(API_ROOT + "/image/" + one.getUsername() + "/" + one.getUsername() + ".jpg"))
        .andExpect(status().isOk());
  }

  @Test
  void getTempProfileImage() throws Exception {
    mockMvc
        .perform(get(API_ROOT + "/image/profile/" + one.getUsername()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:update"})
  void updateProfileImage() throws Exception {
    FileInputStream avatar = new FileInputStream(AVATAR_JPG);
    MockMultipartFile multipartFile =
        new MockMultipartFile("profileImage", "avatar.jpg", IMAGE_JPEG_VALUE, avatar);

    mockMvc
        .perform(
            multipart(API_ROOT + "/updateProfileImage")
                .file(multipartFile)
                .param("username", one.getUsername())
                .contentType(MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath(
                "$.profileImageUrl", Matchers.is("http://localhost/user/image/jdoe/jdoe.jpg")));
  }

  @Test
  @WithMockUser(
      username = "admin",
      authorities = {"user:update"})
  void updateProfileImageWithNotAnImage() throws Exception {
    FileInputStream avatar = new FileInputStream(AVATAR_JPG);
    MockMultipartFile multipartFile =
        new MockMultipartFile("profileImage", "avatar.txt", TEXT_PLAIN_VALUE, avatar);

    mockMvc
        .perform(
            multipart(API_ROOT + "/updateProfileImage")
                .file(multipartFile)
                .param("username", one.getUsername())
                .contentType(MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof NotAnImageFileException));
  }

  @Test
  void updateProfileImageNotAuthenticated() throws Exception {
    FileInputStream avatar = new FileInputStream(AVATAR_JPG);
    MockMultipartFile multipartFile =
        new MockMultipartFile("profileImage", "avatar.jpg", IMAGE_JPEG_VALUE, avatar);

    mockMvc
        .perform(
            multipart(API_ROOT + "/updateProfileImage")
                .file(multipartFile)
                .param("username", one.getUsername())
                .contentType(MULTIPART_FORM_DATA))
        .andExpect(status().isForbidden());
  }*/
}

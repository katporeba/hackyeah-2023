package com.hackyeah.sl.backend.resource;

import com.hackyeah.sl.backend.domain.DTO.UserLogin;
import com.hackyeah.sl.backend.domain.DTO.UserRegister;
import com.hackyeah.sl.backend.domain.HttpResponse;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.domain.UserPrincipal;
import com.hackyeah.sl.backend.exception.ExceptionHandling;
import com.hackyeah.sl.backend.exception.domain.*;
import com.hackyeah.sl.backend.service.TokenBlacklistService;
import com.hackyeah.sl.backend.service.UserService;
import com.hackyeah.sl.backend.utilty.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.hackyeah.sl.backend.constant.FileConstant.*;
import static com.hackyeah.sl.backend.constant.SecurityConstant.AUTHORIZATION_HEADER;
import static com.hackyeah.sl.backend.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = {"/user"})
public class UserResource extends ExceptionHandling {

    public static final String NEW_PASSWORD_WAS_SENT_TO = "Email with new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    public static final String USER_LOGGED_OFF = "User logged off successfully";
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegister user)
            throws UserNotFoundException, EmailExistException, UsernameExistException, PasswordsDontMatchException {
        User newUser =
                userService.register(user.getEmail(), user.getPassword(), user.getRepeatPassword());
        return new ResponseEntity<>(newUser, OK);
    }

    @PreAuthorize("hasAnyAuthority('user:create')")
    @PostMapping("/add")
    public ResponseEntity<User> add(
            @NotBlank @Email @RequestParam String email,
            @NotBlank @RequestParam String password,
            @NotBlank @RequestParam String role,
            @NotBlank @RequestParam String isActive,
            @NotBlank @RequestParam String nonLocked,
            @RequestParam(required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {

        User newUser =
                userService.addNewUser(
                        email,
                        password,
                        role,
                        Boolean.parseBoolean(nonLocked),
                        Boolean.parseBoolean(isActive),
                        profileImage);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('user:update') OR #currentUsername == principal")
    @PostMapping("/update")
    public ResponseEntity<User> update(
            @NotBlank @RequestParam String currentUsername,
            @NotBlank @RequestParam String firstName,
            @NotBlank @RequestParam String lastName,
            @NotBlank @RequestParam String username,
            @NotBlank @Email @RequestParam String email,
            @NotBlank @RequestParam String role,
            @NotBlank @RequestParam String isActive,
            @NotBlank @RequestParam String nonLocked,
            @RequestParam(required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {

        User updateUser =
                userService.updateUser(
                        currentUsername,
                        firstName,
                        lastName,
                        username,
                        email,
                        role,
                        Boolean.parseBoolean(nonLocked),
                        Boolean.parseBoolean(isActive),
                        profileImage);

        return new ResponseEntity<>(updateUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody UserLogin user) throws EmailNotFoundException {

        User loginUser = userService.findUserByEmail(user.getEmail());

        if (loginUser == null) {
            throw new EmailNotFoundException("Email does not exist");
        }

        authenticate(loginUser.getEmail(), user.getPassword());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<HttpResponse> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws EmailNotFoundException {
        tokenBlacklistService.blacklistToken(authorization.substring(TOKEN_PREFIX.length()));
        return response(OK, USER_LOGGED_OFF);
    }


    @GetMapping("/find/{email}")
    public ResponseEntity<User> findUser(@NotBlank @PathVariable String email) {
        User foundUser = userService.findUserByEmail(email);
        return new ResponseEntity<>(foundUser, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> userList() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetPassword/{email}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> resetPassword(
            @NotBlank @Email @PathVariable String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_WAS_SENT_TO + email);
    }

    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(
            @NotBlank @PathVariable String email) throws IOException {
        userService.deleteUser(email);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @GetMapping(path = "/image/profile/{email}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@NotBlank @PathVariable String email)
            throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + email);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(
                        httpStatus.value(),
                        httpStatus,
                        httpStatus.getReasonPhrase().toUpperCase(),
                        message.toUpperCase()),
                httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, tokenProvider.generateToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}

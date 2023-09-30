package com.hackyeah.sl.backend.resource;

import com.hackyeah.sl.backend.domain.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static com.hackyeah.sl.backend.constant.Authority.SUPER_ADMIN_AUTHORITIES;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/test"})
public class TestController {

    String name = Arrays.toString(SUPER_ADMIN_AUTHORITIES);

    @GetMapping("/admin")

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> adminEndpointTest() {
        return response(OK, "You are logged as admin.");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<HttpResponse> userEndpointTest() {
        return response(OK, "You are logged as default user");
    }

    @GetMapping("/userDelete")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> checkUserDeleteAuthority() {
        return response(OK, "You have user:delete auth");
    }

    @GetMapping("/userRead")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<HttpResponse> checkUserReadAuth() {
        return response(OK, "You have user:read auth");
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

}

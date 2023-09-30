package com.hackyeah.sl.backend.listeners;


import com.hackyeah.sl.backend.domain.UserPrincipal;
import com.hackyeah.sl.backend.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
  private final LoginAttemptService loginAttemptService;

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    Object principal = event.getAuthentication().getPrincipal();
    if (principal instanceof UserPrincipal user) {
      loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
    }
  }
}

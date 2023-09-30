package com.hackyeah.sl.backend.filter;

import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.enumeration.Role;
import com.hackyeah.sl.backend.repository.UserRepository;
import com.hackyeah.sl.backend.utilty.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hackyeah.sl.backend.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.hackyeah.sl.backend.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
      response.setStatus(OK.value());
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      if (StringUtils.isEmpty(authorizationHeader)
          || !StringUtils.startsWith(authorizationHeader, TOKEN_PREFIX)) {
        filterChain.doFilter(request, response);
        return;
      }
      String token = authorizationHeader.substring(TOKEN_PREFIX.length());
      String username = jwtTokenProvider.getSubject(token);
      if (jwtTokenProvider.isTokenValid(username, token)) {

        List<SimpleGrantedAuthority> role = jwtTokenProvider.getAuthorities(token);


        List<SimpleGrantedAuthority> authorities = Arrays.stream(getRoleEnumName(String.valueOf(role.get(0))).getAuthorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        authorities.addAll(role);

        Authentication authentication =
            jwtTokenProvider.getAuthentication(username, authorities, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        SecurityContextHolder.clearContext();
      }
    }
    filterChain.doFilter(request, response);
  }

  private Role getRoleEnumName(String role) {
    return Role.valueOf(role.toUpperCase());
  }

}

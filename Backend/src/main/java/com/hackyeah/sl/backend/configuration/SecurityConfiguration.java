package com.hackyeah.sl.backend.configuration;

import com.hackyeah.sl.backend.constant.SecurityConstant;
import com.hackyeah.sl.backend.filter.JwtAccessDeniedHandler;
import com.hackyeah.sl.backend.filter.JwtAuthenticationEntryPoint;
import com.hackyeah.sl.backend.filter.JwtAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private JwtAuthorizationFilter authorizationFilter;
    private JwtAccessDeniedHandler accessDeniedHandler;
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationEventPublisher authenticationEventPublisher;


    @Autowired
    public SecurityConfiguration(JwtAuthorizationFilter authorizationFilter, JwtAccessDeniedHandler accessDeniedHandler, JwtAuthenticationEntryPoint authenticationEntryPoint, UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder, AuthenticationEventPublisher authenticationEventPublisher) {
        this.authorizationFilter = authorizationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationEventPublisher = authenticationEventPublisher;
    }

    @Value("${frontend.url}")
    private String frontendUrl;

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationEventPublisher(authenticationEventPublisher);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(SecurityConstant.PUBLIC_URL)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList(frontendUrl));
        corsConfiguration.setAllowedHeaders(
                Arrays.asList(
                        "Origin",
                        "Access-Control-Allow-Origin",
                        "Content-Type",
                        "Accept",
                        "Jwt-Token",
                        "Authorization",
                        "Origin, Accept",
                        "X-Requested-With",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(
                Arrays.asList(
                        "Origin",
                        "Content-Type",
                        "Accept",
                        "Jwt-Token",
                        "Authorization",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}

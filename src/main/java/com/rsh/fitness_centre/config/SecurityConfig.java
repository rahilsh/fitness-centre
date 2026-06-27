package com.rsh.fitness_centre.config;

import com.rsh.fitness_centre.security.JwtAuthenticationFilter;
import com.rsh.fitness_centre.security.JwtTokenProvider;
import com.rsh.fitness_centre.security.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration with JWT authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private TokenBlacklistService tokenBlacklistService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtTokenProvider, tokenBlacklistService);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.addAllowedOrigin("*");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authz -> authz
            // Public endpoints
            .requestMatchers("/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/search").permitAll()
            .requestMatchers("/auth/register", "/auth/login", "/auth/me", "/auth/refresh").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            
            // Actuator endpoints
            .requestMatchers("/actuator/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
            
            // User endpoints
            .requestMatchers(HttpMethod.POST, "/bookings").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/bookings/**").authenticated()
            .requestMatchers(HttpMethod.GET, "/bookings/**").authenticated()
            
            // Admin endpoints
            .requestMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN", "SUPER_ADMIN")
            .requestMatchers(HttpMethod.POST, "/fitnessCentres").hasAnyRole("ADMIN", "SUPER_ADMIN")
            .requestMatchers(HttpMethod.POST, "/fitnessCentres/*/slots").hasAnyRole("ADMIN", "SUPER_ADMIN")
            .requestMatchers(HttpMethod.GET, "/fitnessCentres/*/slots").hasAnyRole("ADMIN", "SUPER_ADMIN")
            
            // All other requests require authentication
            .anyRequest().authenticated()
        )
        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}

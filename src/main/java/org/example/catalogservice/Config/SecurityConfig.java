package org.example.catalogservice.Config;

import org.example.catalogservice.Exceptions.AdminAuthenticationFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.GET, "/restaurants/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/restaurants/**", "/menu-items/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (!"admin".equals(username)) {
                    throw new AdminAuthenticationFailedException("Username not found");
                }
                return User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("password"))
                        .roles("ADMIN")
                        .build();
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
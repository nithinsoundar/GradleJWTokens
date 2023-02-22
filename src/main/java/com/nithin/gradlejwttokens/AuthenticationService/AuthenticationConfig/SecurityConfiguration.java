package com.nithin.gradlejwttokens.AuthenticationService.AuthenticationConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/user/**", "/api/v1/blog/unrestricted").permitAll()
                .requestMatchers("/api/v1/blog/restricted").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated();
        return http.build();
    }

}

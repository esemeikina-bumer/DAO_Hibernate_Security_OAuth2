package ru.netology.DAO_Hibernate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
prePostEnabled = true,
securedEnabled = true,
jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/persons").permitAll()
                        // Всё остальное — только после логина
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout.permitAll());

        return http.build();
    }
    @Bean
    public UserDetailsService users() {
        UserDetails reader = User.builder()
                .username("reader")
                .password("{noop}reader123")
                .roles("READ")
                .build();

        UserDetails writer = User.builder()
                .username("writer")
                .password("{noop}writer123")
                .roles("READ", "WRITE")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin123")
                .roles("READ", "WRITE", "DELETE")
                .build();

        return new InMemoryUserDetailsManager(reader, writer, admin);
    }
}
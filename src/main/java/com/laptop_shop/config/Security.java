package com.laptop_shop.config;

import com.laptop_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class Security {

    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên tĩnh & auth
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // Tất cả URL /admin/** chỉ dành cho ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Trang CRUD product/category cũng chỉ dành cho ADMIN
                .requestMatchers("/products/**", "/categories/**").hasRole("ADMIN")
                // Cart và checkout CHỈ cho USER (không phải ADMIN)
                .requestMatchers("/cart/**", "/checkout/**", "/my-orders").hasRole("USER")
                // Các trang public
                .requestMatchers("/", "/shop", "/product/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)   // Redirect theo role
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}

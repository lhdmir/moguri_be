package ync.likelion.moguri_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ync.likelion.moguri_be.filter.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/login/**", "/api/register").permitAll() // 로그인 관련 엔드포인트는 인증 없이 접근 가능
                        .requestMatchers("/api/**").authenticated() // 나머지 /api/** 요청은 인증 필요
                        .anyRequest().authenticated()); // 그 외의 요청은 인증 필요

        // JWT 필터 추가
        http.addFilterBefore(JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtRequestFilter JwtRequestFilter() {
        return new JwtRequestFilter(); // JWT 필터 구현 클래스
    }
}

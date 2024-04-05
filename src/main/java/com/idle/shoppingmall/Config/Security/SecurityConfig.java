package com.idle.shoppingmall.Config.Security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.shoppingmall.Service.LoginService;
import com.idle.shoppingmall.Service.User.UserDetailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final UserDetailService userDetailService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable) //csrf 설정 disable
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/api/GET/login").permitAll()
                                .requestMatchers("/api/POST/login").permitAll()
                                .requestMatchers("/api/GET/logout").permitAll()
                                .requestMatchers("/FE/cart").hasAnyRole("USER")
                                .requestMatchers("/FE/order").hasAnyRole("USER")
                                .anyRequest().permitAll()
                )
                .formLogin((formLogin) ->
                        formLogin
                                .loginPage("/FE/login") //로그인 화면 설정
                                .loginProcessingUrl("/api/POST/login") // login submit 요청을 받을 url
                                .successHandler(new CustomAuthenticationSuccessHandler(
                                        loginService
                                ))
                                .defaultSuccessUrl("/FE/main")
                                .failureHandler(new CustomAuthenticationFailureHandler())
                                .failureUrl("/FE/login") //로그인 실패시 이동할 url
                )
                .logout((logoutConfig)->
                        logoutConfig
                                .logoutUrl("/api/POST/logout")
                                .logoutSuccessUrl("/productList") //로그아웃 성공시 이동할 url
                                .addLogoutHandler(new CustomLogoutHandler())
                ).userDetailsService(userDetailService);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final AuthenticationEntryPoint unauthorizedEntryPoint =
            //사용자가 인증되지 않은 상태에서 보호된 리소스에 접근할 때 호출
            (request, response, authException) -> {
                //응답을 401로 설정하고 메시지를 담는다.
                ErrorResponse fail = new ErrorResponse(HttpStatus.UNAUTHORIZED, "로그인을 해주세여");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE); //응답의 타입을 json으로 설정
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    private final AccessDeniedHandler accessDeniedHandler =
            //사용자가 권한이 없는 상태에서 보호된 리소스에 접근할 때 호출
            (request, response, accessDeniedException) -> {
                //응답을 403으로 설정하고 메시지를 담는다.
                ErrorResponse fail = new ErrorResponse(HttpStatus.FORBIDDEN, "권한이 없어요");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    @Getter
    @RequiredArgsConstructor
    public class ErrorResponse {
        private final HttpStatus status;
        private final String message;
    }

}

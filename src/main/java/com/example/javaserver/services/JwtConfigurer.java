package com.example.javaserver.services;


import com.example.javaserver.security.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenService tokenService;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtTokenFilter filter = new JwtTokenFilter(tokenService);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sm-> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
}

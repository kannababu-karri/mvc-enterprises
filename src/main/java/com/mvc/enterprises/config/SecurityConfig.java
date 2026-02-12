package com.mvc.enterprises.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mvc.enterprises.utils.Utils;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        _LOGGER.info(">>> SecurityFilterChain bean is being created <<<");
    	
        http
        .csrf(csrf -> csrf.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())   // IMPORTANT
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/actuator/**").hasRole(Utils.getAdminRole()) // rest secured
                .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    JwtFilter jwtFilter() {
        return new JwtFilter();
    }
    
    //Completely bypass Spring Security for these URLs
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/login",
                        "/loginHome",
                        "/logout",
                        "/WEB-INF/**",
                        "/styles/**",
                        "/scripts/**",
                        "/images/**",
                        "/actuator/health" // public
                );
    }
}


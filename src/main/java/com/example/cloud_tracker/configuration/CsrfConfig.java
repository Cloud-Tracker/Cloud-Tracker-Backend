//package com.example.cloud_tracker.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
//
//import com.example.cloud_tracker.filter.CsrfCookieFilter;
//
//@Configuration
//public class CsrfConfig {
//
//    @Bean
//    public SecurityFilterChain csrfFilterChain(HttpSecurity http) throws Exception {
//        http
//            // CSRF configuration here
//            .csrf((csrf) -> csrf
//                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
//                .ignoringRequestMatchers("/") // Ignore CSRF protection for the root path
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            )
//            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//            .authorizeHttpRequests((authz) -> authz
//                .anyRequest().authenticated()
//            );
//
//        return http.build();
//    }
//}
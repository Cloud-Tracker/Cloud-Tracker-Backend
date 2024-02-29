package com.example.cloud_tracker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.cloud_tracker.filter.CsrfCookieFilter;
import com.example.cloud_tracker.filter.JwtFilter;

/*
 * the explaination of the crsf and how it work is : 
 * we have a public endpoint /hello that we want to ignore the csrf token for it (we can add more endpoints to ignore the csrf token for it)
 * we have a filter that will add the csrf token to the response header
 * and the csrf token will be added to the cookie and the response header
 * and every request will be checked for the csrf token and if it is not there it will return 403 
 * we add the csrf token to the request header in the form of X-XSRF-TOKEN = csrf token
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired 
    private AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        http
        .cors(cors -> cors.configurationSource(new CorsConfig()))

        // i put the csrf config in the csrfconfig class and i will call it here
        .csrf((csrf) -> csrf.getClass().equals(CsrfConfig.class))

        .authorizeHttpRequests((authz) -> authz
                .anyRequest().permitAll())
        .authorizeHttpRequests(requests -> requests
                .anyRequest()
                .authenticated())
        .sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}

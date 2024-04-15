package com.example.cloud_tracker.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.cloud_tracker.filter.JwtFilter;
import com.example.cloud_tracker.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

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

    private final JwtFilter jwtFilter;

    private final LogoutHandler logoutHandler;

    public SecurityConfig(JwtFilter jwtFilter,LogoutHandler logoutHandler) {
        this.jwtFilter = jwtFilter;
        this.logoutHandler=logoutHandler;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        http

                .cors(cors -> cors.configurationSource(new CorsConfig()))
//
//        // i put the csrf config in the csrfconfig class and i will call it here
//        .csrf((csrf) -> csrf.getClass().equals(CsrfConfig.class));

                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/", "/error", "/webjars/**", "/index.html", "/signup","/blogs/blog","/blogs","/blogs/blog/**"
                                , "/signin").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oath2 ->{
                    oath2.successHandler((request, response, authentication) -> {
                        response.sendRedirect("/welcome.html");
                    });
                })
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
               .logout((logout) ->logout
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request,response,authentication)->{
                    SecurityContextHolder.clearContext();
                    response.getWriter().write("Logout successful");
                })

               );
        return http.build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

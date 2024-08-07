package com.example.cloud_tracker.jwt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.cloud_tracker.filter.JwtFilter;
import com.example.cloud_tracker.service.JwtService;
import com.example.cloud_tracker.service.UserDetailsServiceImpl;
import init.UserInit;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringJUnitConfig
public class JwtFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    public void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);
        jwtFilter = new JwtFilter();
        jwtFilter.setJwtService(jwtService);
        jwtFilter.setUserDetailsService(userDetailsService);
    }

    @Test
    public void testShouldNotFilter() throws ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURI("/test");
        assertFalse(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/webjars/some-resource");
        assertTrue(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/index.html");
        assertTrue(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/signup");
        assertTrue(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/signin");
        assertTrue(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/");
        assertTrue(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/welcome.html");
        assertTrue(jwtFilter.shouldNotFilter(request));

        request.setRequestURI("/blogs");
        assertTrue(jwtFilter.shouldNotFilter(request));
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails user = UserInit.createUser();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        String validToken = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUserName(validToken)).thenReturn("tst@example.com");
        when(userDetailsService.loadUserByUsername("tst@example.com")).thenReturn(user);
        when(jwtService.validateToken(validToken, user)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NotBearerToken() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails user = UserInit.createUser();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        String validToken = "validToken";
        when(request.getHeader("Authorization")).thenReturn(validToken);
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token does not begin with Bearer String");
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_NULLToken() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails user = UserInit.createUser();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        String validToken = null;
        when(request.getHeader("Authorization")).thenReturn(validToken);
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token does not begin with Bearer String");
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_NULLUserEmail() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails user = UserInit.createUser();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        String validToken = "Bearer test";
        when(request.getHeader("Authorization")).thenReturn(validToken);
        jwtFilter.doFilterInternal(request, response, filterChain);
        verifyNoInteractions(filterChain);
    }

    // @Test
    // void testDoFilterInternal_InValidToken() throws ServletException, IOException {
    //     HttpServletRequest request = mock(HttpServletRequest.class);
    //     HttpServletResponse response = mock(HttpServletResponse.class);
    //     FilterChain filterChain = mock(FilterChain.class);
    //     UserDetails user = UserInit.createUser();
    //     UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
    //             user.getAuthorities());
    //     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    //     String validToken = "validToken";
    //     when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
    //     when(jwtService.extractUserName(validToken)).thenReturn("tst@gmail.com");
    //     when(userDetailsService.loadUserByUsername("tst@gmail.com")).thenReturn(user);
    //     when(jwtService.validateToken(validToken, user)).thenReturn(false);

    //     jwtFilter.doFilterInternal(request, response, filterChain);
    //     verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is not valid");
    //     verifyNoInteractions(filterChain);
    // }

}

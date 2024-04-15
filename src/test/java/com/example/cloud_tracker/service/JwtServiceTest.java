package com.example.cloud_tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.cloud_tracker.model.BlackListedTokens;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.repository.BlackListedTokensRepository;

import init.UserInit;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringJUnitConfig
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private BlackListedTokensRepository blackListedTokensRepository;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void ExtractUserNameTest() {
        UserDetails user = UserInit.createUser();
        String token = jwtService.generateToken(user);
        String  S = jwtService.extractUserName(token);
        System.out.println("String :" + S);
        assertEquals(user.getUsername(), jwtService.extractUserName(token));
    }

    @Test
    void validateTokenTest(){
        UserDetails user = UserInit.createUser();
        String token = jwtService.generateToken(user);
        when(blackListedTokensRepository.existsByToken(token)).thenReturn(false);
        assert(jwtService.validateToken(token, user));
    }
    
    @Test
    void validateTokenTestFail(){
        UserDetails user = UserInit.createUser();
        UserDetails user2 = new User(1, "tst@test.com", "test", "test", null, null);
        String token = jwtService.generateToken(user);
        when(blackListedTokensRepository.existsByToken(token)).thenReturn(false);
        assert(!jwtService.validateToken(token, user2));
    }

    @Test
    void validateTokenTestFailRevokedToken(){
        UserDetails user = UserInit.createUser();
        String token = jwtService.generateToken(user);
        when(blackListedTokensRepository.existsByToken(token)).thenReturn(true);
        assert(!jwtService.validateToken(token, user));
    }

    @Test
    void validateTokenTestExpired(){
        UserDetails user = UserInit.createUser();
        Map<String , Object>extraClaims = new HashMap<>();
        String token = Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date (System.currentTimeMillis()))
        .setExpiration(new Date (System.currentTimeMillis() - 1000))
        .signWith(jwtService.getSigningKey(),SignatureAlgorithm.HS256)
        .compact();
        assert(!jwtService.validateToken(token, user));
    }

    @Test
    void getSigningKeyTest(){
        byte[] keyBytes = Decoders.BASE64.decode("1b7b9849719e40adc0c02f742f498ea5f1b0c468cd37be53429142565b1df374");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        assertEquals(key, jwtService.getSigningKey());
    }

    @Test
    void extractAllClaimsTest(){
        UserDetails user = UserInit.createUser();
        String token = jwtService.generateToken(user);
        Claims claims = Jwts.parserBuilder()
        .setSigningKey(jwtService.getSigningKey())
        .build()
        .parseClaimsJws(token).getBody();
        assertEquals(claims, jwtService.extractAllClaims(token));
    }

    @Test
    void generateTokenTest(){
        UserDetails user = UserInit.createUser();
        Map<String , Object>extraClaims = new HashMap<>();
        String token = jwtService.generateToken(user);

        String tok = Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date (System.currentTimeMillis()))
        .setExpiration(new Date (System.currentTimeMillis() + 1000 * 60 * 60 * 24))
        .signWith(jwtService.getSigningKey(),SignatureAlgorithm.HS256)
        .compact();
        assertEquals(token, tok);

    }

    @Test
    void refreshTokenTest(){
        UserDetails user = UserInit.createUser();
        Map<String , Object>extraClaims = new HashMap<>();
        String token = jwtService.generateRefreshToken(user);
        String tok = Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date (System.currentTimeMillis()))
        .setExpiration(new Date (System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
        .signWith(jwtService.getSigningKey(),SignatureAlgorithm.HS256)
        .compact();
        assertEquals(token, tok);

    }

    @Test
    public void testRevokeTokenSuccess() {
        when(request.getHeader("Authorization")).thenReturn("Bearer test");
        jwtService.blackListToken(request);
        verify(blackListedTokensRepository).save(any(BlackListedTokens.class));
    }

    @Test
    public void testRevokeTokenFailNullHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtService.blackListToken(request);
        verify(blackListedTokensRepository, never()).save(any());
    }

    @Test
    public void testRevokeTokenFailInvalidHeader() {
        when(request.getHeader("Authorization")).thenReturn("tesy");
        jwtService.blackListToken(request);
        verify(blackListedTokensRepository, never()).save(any());
    }
    
}

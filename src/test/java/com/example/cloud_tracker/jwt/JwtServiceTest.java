package com.example.cloud_tracker.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import init.UserInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.cloud_tracker.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringJUnitConfig
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
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
        assert(jwtService.validateToken(token, user));
    }

    @Test
    void ExtractExpirationTest() {
        UserDetails user = UserInit.createUser();
        String token = jwtService.generateToken(user);
        Long time = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        assertEquals(time - time % 1000 , jwtService.extractExpiration(token).getTime());
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
    
}

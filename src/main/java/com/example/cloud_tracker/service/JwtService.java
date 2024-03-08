package com.example.cloud_tracker.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {


    private static final String SECRET_KEY="1b7b9849719e40adc0c02f742f498ea5f1b0c468cd37be53429142565b1df374";

    public String extractUserName(String token) {
        return extractClaim(token, Claims :: getSubject);
    }

    public <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    
    public Claims extractAllClaims(String token){
        return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
    
    public Key getSigningKey(){
        
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(UserDetails userDetails){
        Map<String , Object>extraClaims = new HashMap<>();
        return generateToken(extraClaims, userDetails);
    }
    
    public String generateToken(Map<String , Object>extraClaims,UserDetails userDetails){
        return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 Day
        .signWith(getSigningKey(),SignatureAlgorithm.HS256)
        .compact();
    }
    
    public String generateRefreshToken( UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateRefreshToken(claims, userDetails);
    }
    
    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 Days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}

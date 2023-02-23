package com.nithin.gradlejwttokens.AuthenticationService.config;

import com.nithin.gradlejwttokens.AuthenticationService.Model.LoginCreds;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "6A576E5A7234753778214125442A472D4B6150645367556B5870327335763879";

    @Value("${app.token.message}")
    private String message;

    public String  generateToken(LoginCreds loginCreds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", loginCreds.getRole());
        String token =  Jwts.builder()
                .setClaims(claims)
                .setSubject(loginCreds.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        /*Map<String, String> jwTokenGen = new HashMap<>();
        jwTokenGen.put("token", token);
        jwTokenGen.put("message", message);*/
        return token;


        /*List<String> authorities = Arrays.asList(String.valueOf(loginCreds.getRole()));

        = List.of(new SimpleGrantedAuthority(String.valueOf(loginCreds.getRole())));
        Claims claims = Jwts.claims().setSubject(loginCreds.getUsername());
        claims.put("authorities", authorities.stream().map(s -> new
        SimpleGrantedAuthority(s.getAuthority())).collect(Collectors.toList()));
*/
        /*String jwtToken = Jwts.builder()
                .setSubject(loginCreds.getUsername())
                .claim("authorities", authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))

                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();*/


    }
}
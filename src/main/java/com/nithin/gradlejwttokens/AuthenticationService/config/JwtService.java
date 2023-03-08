package com.nithin.gradlejwttokens.AuthenticationService.config;

import com.nithin.gradlejwttokens.AuthenticationService.Model.LoginCreds;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "6A576E5A7234753778214125442A472D4B6150645367556B5870327335763879";

    @Value("${app.token.message}")
    private String message;

    public Map<String, String>  generateToken(LoginCreds loginCreds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", "ROLE_"+loginCreds.getRole());
        String token =  Jwts.builder()
                .setClaims(claims)
                .setSubject(loginCreds.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        Map<String, String> jwTokenGen = new HashMap<>();
        jwTokenGen.put("token", token);
        jwTokenGen.put("message", message);
        return jwTokenGen;
    }
}
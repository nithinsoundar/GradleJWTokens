package com.nithin.gradlejwttokens.AuthenticationService.config;

import com.nithin.gradlejwttokens.AuthenticationService.User.LoginCreds;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtGeneratorImpl {

    private static final String SECRET_KEY =
            "6A576E5A7234753778214125442A472D4B6150645367556B5870327335763879";

    @Value("${app.jwttoken.message}")
    private String message;

    public Map<String, String> generateToken(LoginCreds loginCreds) {
        String jwtToken="";
        jwtToken = Jwts.builder().setSubject(loginCreds.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        jwtTokenGen.put("message", message);
        return jwtTokenGen;


    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

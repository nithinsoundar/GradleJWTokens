package com.nithin.gradlejwttokens.AuthenticationService.config;

import com.nithin.gradlejwttokens.AuthenticationService.Model.LoginCreds;
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
public class JwtGenerator {

    private static final String SECRET_KEY =
            "6A576E5A7234753778214125442A472D4B6150645367556B5870327335763879";

    @Value("${app.token.message}")
    private String message;

    public Map<String, String> generateToken(LoginCreds loginCreds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginCreds.getUsername());
        claims.put("role", loginCreds.getRole());
        String jwtToken = Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
        Map<String, String> jwTokenGen = new HashMap<>();
        jwTokenGen.put("token", jwtToken);
        jwTokenGen.put("message", message);
        return jwTokenGen;


    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

package com.nithin.gradlejwttokens.AuthenticationService.config;

import java.util.Base64;

public class PasswordUtils {
    public static String decodePassword(String encodedPassword) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
        return new String(decodedBytes);
    }

    public static String encodePassword(String decodedPassword){
        String encodedString =Base64.getEncoder().encodeToString(decodedPassword.getBytes());
        return new String(encodedString);
    }
}

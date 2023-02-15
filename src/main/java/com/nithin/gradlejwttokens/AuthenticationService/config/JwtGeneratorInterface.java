package com.nithin.gradlejwttokens.AuthenticationService.config;

import com.nithin.gradlejwttokens.AuthenticationService.User.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface JwtGeneratorInterface {
    Map<String, String> generateToken(User user);
}

package com.nithin.gradlejwttokens.AuthenticationService.config;

import com.nithin.gradlejwttokens.AuthenticationService.User.User;

import java.util.Map;

public interface JwtGeneratorInterface {

    Map<String, String> generateToken(User user);
}
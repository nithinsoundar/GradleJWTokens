package com.nithin.gradlejwttokens.AuthenticationService.Model;

import lombok.Data;

@Data
public class LoginCreds {
    private String username;
    private String password;
    private Role role;


}

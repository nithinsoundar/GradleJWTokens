package com.nithin.gradlejwttokens.AuthenticationService.Model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class LoginCreds {
    private String username;
    private String password;
    @Field("role")
    private Role role;


}

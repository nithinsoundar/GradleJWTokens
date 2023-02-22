package com.nithin.gradlejwttokens.AuthenticationService.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;

    @Field("role")
    private Role role;
}

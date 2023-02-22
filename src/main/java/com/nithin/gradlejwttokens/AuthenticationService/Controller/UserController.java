package com.nithin.gradlejwttokens.AuthenticationService.Controller;

import com.nithin.gradlejwttokens.AuthenticationService.Model.LoginCreds;
import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.nithin.gradlejwttokens.AuthenticationService.config.JwtGenerator;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import com.nithin.gradlejwttokens.AuthenticationService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nithin.gradlejwttokens.AuthenticationService.config.PasswordUtils.encodePassword;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public UserController(UserService userService, JwtGenerator jwtGenerator){
        this.userService=userService;
        this.jwtGenerator=jwtGenerator;
    }

    @PostMapping("/register")
    public ResponseEntity<?> postUser(@RequestBody User user){
        try{
            user.setPassword(encodePassword(user.getPassword()));
            userService.saveUser(user);
            String registered = "You've been Successfully Registered";
            return new ResponseEntity<>(registered, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginCreds loginCreds) {
        try {
            if(loginCreds.getUsername() == null || loginCreds.getPassword() == null) {
                String empty = "One of the fields are empty. Please Fill it in.";
                return new ResponseEntity<>(empty, HttpStatus.CONFLICT);
            }
            User userData = userService.getUserByNameAndPassword(loginCreds.getUsername(),
                    loginCreds.getPassword());
            if(userData == null){
                String invalid = "You've entered incorrect credentials.";
                return new ResponseEntity<>(invalid, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(jwtGenerator.generateToken(loginCreds), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
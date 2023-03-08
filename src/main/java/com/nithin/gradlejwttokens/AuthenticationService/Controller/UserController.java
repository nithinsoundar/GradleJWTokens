package com.nithin.gradlejwttokens.AuthenticationService.Controller;

import com.nithin.gradlejwttokens.AuthenticationService.MailConfig.MailConfiguration;
import com.nithin.gradlejwttokens.AuthenticationService.Model.LoginCreds;
import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.nithin.gradlejwttokens.AuthenticationService.config.JwtService;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import com.nithin.gradlejwttokens.AuthenticationService.repository.UserRepository;
import com.nithin.gradlejwttokens.AuthenticationService.service.UserService;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nithin.gradlejwttokens.AuthenticationService.config.PasswordUtils.encodePassword;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final MailConfiguration mailConfiguration;
    private final JwtService jwtGenerator;

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, MailConfiguration mailConfiguration, JwtService jwtGenerator, UserRepository userRepository){
        this.userService=userService;
        this.mailConfiguration = mailConfiguration;
        this.jwtGenerator=jwtGenerator;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> postUser(@RequestBody User user){
        try{
            if (userRepository.findByUsername(user.getUsername()) == null &&
                    mailConfiguration.isValidEmail(user.getEmail())){
                user.setPassword(encodePassword(user.getPassword()));
                String role = WordUtils.capitalizeFully(String.valueOf(user.getRole()));
                String newline = System.lineSeparator();
                mailConfiguration.sendEmail(user.getEmail(), "Welcome "+ user.getUsername(),
                        "Hello "+user.getUsername()+"."+"\r\n" +"We are happy to have you onboard! You've been registered as a/an "+ role);
                userService.saveUser(user);
                String registered = "Hi " + user.getUsername() +". You've been Successfully Registered";
                return new ResponseEntity<>(registered, HttpStatus.CREATED);
            }
            else {
                String exists = "Invalid Email-id or User already exists with the name: " + user.getUsername();
                return new ResponseEntity<>(exists, HttpStatus.CONFLICT);
            }
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
            if (loginCreds.getRole()!= null){
                userData = userService.getUserByRole(loginCreds.getUsername(),loginCreds.getRole());
            }
            if(userData == null){
                String invalid = "You've entered incorrect credentials.";
                return new ResponseEntity<>(invalid, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(jwtGenerator.generateToken(loginCreds), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user)
            throws UserNotFoundException {
//        String name = WordUtils.capitalizeFully(username);
        User userData = userService.getUserById(id);
        if (userData.getUsername().equals(user.getUsername())){

            userData.setUsername(user.getUsername());
            userData.setEmail(user.getEmail());
            String password = encodePassword(user.getPassword());
            userData.setPassword(password);
            userData.setRole(user.getRole());

            userService.saveUser(userData);

            return ResponseEntity.ok(userData);
        }
        else {
            String message = "You can update only your details. Please check your username.";
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }

    }


}
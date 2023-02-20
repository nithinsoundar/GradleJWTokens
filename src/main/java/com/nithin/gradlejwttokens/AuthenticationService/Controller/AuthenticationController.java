package com.nithin.gradlejwttokens.AuthenticationService.Controller;

import com.nithin.gradlejwttokens.AuthenticationService.User.LoginCreds;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.RowSet;

@RestController
@RequestMapping("api/v1/blog")
public class AuthenticationController {
    public LoginCreds loginCreds;
    @GetMapping("/unrestricted")
    public ResponseEntity<?> getMessage() {
        LoginCreds logincreds = new LoginCreds();
        return new ResponseEntity<>("Hello "+ logincreds.getUsername(), HttpStatus.OK);
    }
    @GetMapping("/restricted")
    public ResponseEntity<?> getRestrictedMessage() {
        return new ResponseEntity<>("This is a restricted message", HttpStatus.OK);
    }
}

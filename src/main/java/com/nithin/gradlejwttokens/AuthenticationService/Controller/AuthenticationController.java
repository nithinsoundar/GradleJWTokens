package com.nithin.gradlejwttokens.AuthenticationService.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class AuthenticationController {

    @GetMapping("/unrestricted")
    public ResponseEntity<?> getMessage() {
        return new ResponseEntity<>("Hello, this is an unrestricted message.", HttpStatus.OK);
    }
    @GetMapping("/restricted")
    public ResponseEntity<?> getRestrictedMessage(String username) {
        return new ResponseEntity<>(" This is a restricted message!", HttpStatus.OK);
    }
}

package com.nithin.gradlejwttokens.AuthenticationService.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String exception) {
        System.out.println(exception);
    }
}

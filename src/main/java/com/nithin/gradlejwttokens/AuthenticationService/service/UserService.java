package com.nithin.gradlejwttokens.AuthenticationService.service;

import com.nithin.gradlejwttokens.AuthenticationService.User.User;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public void saveUser(User user);
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException;
}

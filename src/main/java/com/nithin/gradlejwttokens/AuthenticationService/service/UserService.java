package com.nithin.gradlejwttokens.AuthenticationService.service;

import com.nithin.gradlejwttokens.AuthenticationService.Model.Role;
import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public void saveUser(User user);
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException;

    User getUserByRole(String username, Role role) throws UserNotFoundException;

    User getUserByUsername(String username) throws UserNotFoundException;

    User getUserById(String id) throws UserNotFoundException;
}

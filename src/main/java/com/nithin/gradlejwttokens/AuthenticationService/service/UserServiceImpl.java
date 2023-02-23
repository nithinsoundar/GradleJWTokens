package com.nithin.gradlejwttokens.AuthenticationService.service;

import com.nithin.gradlejwttokens.AuthenticationService.Model.Role;
import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import com.nithin.gradlejwttokens.AuthenticationService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.nithin.gradlejwttokens.AuthenticationService.config.PasswordUtils.encodePassword;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException {
        password = encodePassword(password);
        User user = userRepository.findByUsernameAndPassword(name, password);
        if(user == null){
            throw new UserNotFoundException("Invalid id and password");
        }
        return user;
    }

    @Override
    public User getUserByRole(String username, Role role) throws UserNotFoundException {
        User user = userRepository.findByUsernameAndRole(username, role);
        if(user == null){
            throw new UserNotFoundException("Invalid id and password");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UserNotFoundException("Invalid id and password");

        }
        return user;
    }

    @Override
    public User getUserById(String id) throws UserNotFoundException {
        User user = userRepository.findById(id).get();
        if(user == null){
            throw new UserNotFoundException("Invalid id and password");
        }
        return user;
    }
}

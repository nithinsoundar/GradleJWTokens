package com.nithin.gradlejwttokens.AuthenticationService.service;

import com.nithin.gradlejwttokens.AuthenticationService.User.User;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import com.nithin.gradlejwttokens.AuthenticationService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByEmailAndPassword(String email, String password) throws UserNotFoundException {
        User user = userRepository.findByEmailAndPassword(email, password);
        if(user == null){
            throw new UserNotFoundException("Invalid id and password");
        }
        return user;
    }
}

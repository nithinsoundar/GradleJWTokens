package com.nithin.gradlejwttokens.AuthenticationService.repository;

import com.nithin.gradlejwttokens.AuthenticationService.User.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {

    User findByEmailAndPassword(String email, String password);

}

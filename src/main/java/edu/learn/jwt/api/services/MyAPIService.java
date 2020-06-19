package edu.learn.jwt.api.services;

import edu.learn.jwt.auth.entities.User;
import edu.learn.jwt.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyAPIService {

    private final UserRepository userRepository;

    @Autowired
    public MyAPIService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getSomeValue() {

        Optional<User> loadedUser = userRepository.findById("TestingUser");

        return loadedUser.map(u -> "The user is " + u.getUsername())
                .orElse("Service layer works but repository doesnt!");
    }

}

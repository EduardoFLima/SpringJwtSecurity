package edu.learn.jwt.auth.services;

import edu.learn.jwt.auth.entities.User;
import edu.learn.jwt.auth.repositories.UserRepository;
import edu.learn.jwt.auth.utils.JwtUtils;
import edu.learn.jwt.auth.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public Optional<String> createToken(User user) {
        return authenticate(user.getUsername(), user.getPassword())
                .map(u -> jwtUtils.generateToken(user.getUsername()));
    }

    private Optional<User> authenticate(String username, String password) {
        return userRepository.findById(username)
                .filter(user -> PasswordUtils.isValid(password, user.getPassword()));
    }
}

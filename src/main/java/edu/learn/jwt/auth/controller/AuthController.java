package edu.learn.jwt.auth.controller;


import edu.learn.jwt.auth.entities.User;
import edu.learn.jwt.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import static edu.learn.jwt.auth.utils.Role.NORMAL;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping(value = "/getToken")
    public ResponseEntity<AuthResponse> getToken(@RequestHeader(name = "username") String username,
                                                 @RequestHeader(name = "password") String password) {
        return service.createToken(new User(username, password, NORMAL))
                .map(token -> {
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticationManager.authenticate(
                                    new UsernamePasswordAuthenticationToken(username, password)
                            ) );

                    return token;
                })
                .map(token -> ResponseEntity.ok()
                        .body(AuthResponse.builder()
                                .withToken(token)
                                .build()))
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}

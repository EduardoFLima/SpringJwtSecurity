package edu.learn.jwt.auth.controller;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "with")
public class AuthResponse {

    private String token;
}

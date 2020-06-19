package edu.learn.jwt.api.controller;


import edu.learn.jwt.api.services.MyAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyAPIController {

    private final MyAPIService service;

    @Autowired
    public MyAPIController(MyAPIService service) {
        this.service = service;
    }

    @GetMapping(value = "/main")
    public ResponseEntity<?> mainEndpoint() {
        return ResponseEntity.ok().body(service.getSomeValue());
    }
}

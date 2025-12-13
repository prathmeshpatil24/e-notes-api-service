package com.enotes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class HomeController {


    @GetMapping("/home")
    public String profile(Principal principal){
        return principal.getName() + "Welcome....!";
    }
}

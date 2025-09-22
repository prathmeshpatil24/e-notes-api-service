package com.enotes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AppController {

    @GetMapping("/test")
    public String testPage() {
        System.out.println("!!! This is a new print statement !!!");

        return "test-page";
    }
}

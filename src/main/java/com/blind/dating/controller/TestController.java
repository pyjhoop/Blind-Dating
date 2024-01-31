package com.blind.dating.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {



    @GetMapping("/test")
    public Test test(){
        Test test = new Test("ok");
        return test;
    }
}

package com.blind.dating.dto.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chat")
    public String chat(){
        return "chat";
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}

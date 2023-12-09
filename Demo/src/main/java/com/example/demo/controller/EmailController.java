package com.example.demo.controller;


import com.example.demo.model.Email;
import com.example.demo.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping
    public List<Email> fetchAllEmails() {
        return emailService.getAllEmails();
    }
    @PostMapping("/addEmail")
    public Email addEmail(@RequestBody Email email){
        return emailService.addEmail(email);
    }
}

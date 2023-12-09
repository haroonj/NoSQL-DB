package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Email {
    private String _id;
    private String sender;
    private String receiver;
    private String subject;
    private String body;

    public Email(String sender, String receiver, String subject, String body) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
    }
}

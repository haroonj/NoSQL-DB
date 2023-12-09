package com.example.demo.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class  User {
    private String _id;
    private String name;
    private String emailAddress;

    public User(String name, String emailAddress) {
        this.name = name;
        this.emailAddress = emailAddress;
    }
}

package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public List<User> getAllUsers() {
        return userRepository.getAllDocuments();
    }

    public User addUser(User user) {
        return userRepository.createDocument(user);
    }
}

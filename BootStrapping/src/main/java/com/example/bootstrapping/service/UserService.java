package com.example.bootstrapping.service;

import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.model.system.User;
import com.example.bootstrapping.repository.UserRepository;
import com.example.bootstrapping.util.AdminTokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final CommunicationService communicationService;
    private final AdminTokenGenerator adminTokenGenerator;
    private final UserRepository userRepository;

    public UserService(CommunicationService communicationService, AdminTokenGenerator adminTokenGenerator, UserRepository userRepository) {
        this.communicationService = communicationService;
        this.adminTokenGenerator = adminTokenGenerator;
        this.userRepository = userRepository;
    }

    public User registerUser(User user, Node node) {
        user.setRole("user");
        return userRepository.createDocument(communicationService, node, user, adminTokenGenerator.getToken(communicationService, node));
    }
}

package com.example.bootstrapping.controller;


import com.example.bootstrapping.model.request.JwtRequest;
import com.example.bootstrapping.model.response.JwtResponse;
import com.example.bootstrapping.model.response.LoginResponse;
import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.model.system.User;
import com.example.bootstrapping.service.CommunicationService;
import com.example.bootstrapping.service.NodeService;
import com.example.bootstrapping.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final CommunicationService communicationService;
    private final NodeService nodeService;
    private final UserService userService;


    public UserController(CommunicationService communicationService, NodeService nodeService, UserService userService) {
        this.communicationService = communicationService;
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody JwtRequest jwtRequest) {
        return getLoginResponse(jwtRequest);
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody User user) {
        Node loadBalancedNode = nodeService.getNode();
        User regUser = userService.registerUser(user, loadBalancedNode);
        return getLoginResponse(new JwtRequest(regUser.getUsername(), regUser.getPassword()));
    }

    @PostMapping("/logout")
    public void logout(@RequestBody String nodeUrl) {
        nodeService.removeUserFromNode(nodeUrl);
    }

    private LoginResponse getLoginResponse(JwtRequest jwtRequest) {
        Node loadBalancedNode = nodeService.getNode();
        JwtResponse jwtResponse = communicationService.login(loadBalancedNode, jwtRequest);
        nodeService.addUserToNode(loadBalancedNode);
        return new LoginResponse(jwtResponse.getJwtToken(), loadBalancedNode.getUrl());
    }
}

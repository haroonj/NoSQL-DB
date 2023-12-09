package com.example.bootstrapping.util;

import com.example.bootstrapping.model.request.JwtRequest;
import com.example.bootstrapping.model.response.JwtResponse;
import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.service.CommunicationService;
import org.springframework.stereotype.Component;

@Component
public class AdminTokenGenerator {
    private String token = "";

    public String getToken(CommunicationService communicationService, Node node) {
        if (token.isEmpty()) {
            JwtResponse jwtResponse = communicationService.login(node, new JwtRequest("admin", "admin"));
            if (jwtResponse != null) {
                this.token = jwtResponse.getJwtToken();
            }
        }
        return token;
    }
}

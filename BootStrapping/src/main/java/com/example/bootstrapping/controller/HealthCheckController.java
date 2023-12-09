package com.example.bootstrapping.controller;

import com.example.bootstrapping.service.NodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    private final NodeService nodeService;

    public HealthCheckController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        if (nodeService.isReady()) {
            System.out.println("Healthy");
            return ResponseEntity.ok("Healthy");
        } else {
            System.out.println("Unhealthy");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Unhealthy");
        }
    }
}

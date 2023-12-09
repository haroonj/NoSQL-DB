package com.example.node.controller;

import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.services.AffinityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping("/affinity")
public class AffinityController {
    private final AffinityService affinityService;

    public AffinityController(AffinityService affinityService) {
        this.affinityService = affinityService;
    }

    @PostMapping("/processAffinity")
    public ResponseEntity<QueryResponse> processAffinity(@RequestBody QueryRequest queryRequest) {
        log.info("processAffinity " + queryRequest.toString());
        QueryResponse queryResponse = affinityService.processAffinity(queryRequest);
        return ResponseEntity.status(queryResponse.getStatus()).body(queryResponse);
    }
}

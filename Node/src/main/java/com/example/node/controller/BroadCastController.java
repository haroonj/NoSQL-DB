package com.example.node.controller;

import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.services.BroadCastService;
import com.example.node.services.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping("/broadCast")
public class BroadCastController {
    private final QueryService queryService;
    private final BroadCastService broadCastService;

    public BroadCastController(QueryService queryService, BroadCastService broadCastService) {
        this.queryService = queryService;
        this.broadCastService = broadCastService;
    }

    @PostMapping("/processQuery")
    public ResponseEntity<QueryResponse> processQuery(@RequestBody QueryRequest queryRequest) {
        log.info(queryRequest.toString());
        QueryResponse queryResponse = queryService.processQuery(queryRequest);
        return ResponseEntity.status(queryResponse.getStatus()).body(queryResponse);
    }

    @PostMapping("/processForwardedQuery")
    public ResponseEntity<QueryResponse> processForwardedQuery(@RequestBody QueryRequest queryRequest) {
        log.info(queryRequest.toString());
        QueryResponse queryResponse = queryService.processQuery(queryRequest);
        broadCastService.broadCastChanges(queryResponse, queryRequest);
        return ResponseEntity.status(queryResponse.getStatus()).body(queryResponse);
    }

    @PostMapping("/reflectAffinity")
    public ResponseEntity<QueryResponse> reflectAffinity(@RequestBody QueryRequest queryRequest) {
        log.info(queryRequest.toString());
        QueryResponse queryResponse = broadCastService.reflectAffinity(queryRequest);
        return ResponseEntity.status(queryResponse.getStatus()).body(queryResponse);
    }
}

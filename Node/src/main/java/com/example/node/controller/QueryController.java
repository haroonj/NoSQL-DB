package com.example.node.controller;

import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.services.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping("/query")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/processQuery")
    public ResponseEntity<QueryResponse> processQuery(@RequestBody QueryRequest queryRequest){
        log.info(queryRequest.toString());
        QueryResponse queryResponse = queryService.processQuery(queryRequest);
        return ResponseEntity.status(queryResponse.getStatus()).body(queryResponse);
    }
}

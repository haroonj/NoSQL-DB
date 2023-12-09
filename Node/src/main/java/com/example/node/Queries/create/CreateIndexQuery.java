package com.example.node.Queries.create;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.IndexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;

@Slf4j
public class CreateIndexQuery implements Query {

    private final IndexUtil indexUtil;

    public CreateIndexQuery() {
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            if (indexUtil.createPropertyIndex(queryRequest.getDatabase(), queryRequest.getCollection(), queryRequest.getBody().get("indexedBy").toString())) {
                return QueryResponse.builder()
                        .message("Create an index on " + queryRequest.getBody().get("indexedBy").toString() + " collection name " + queryRequest.getCollection() + "in database name " + queryRequest.getDatabase())
                        .status(201)
                        .build();
            }
            return QueryResponse.builder()
                    .message("The Index Already exists")
                    .status(302)
                    .build();
        } catch (FileNotFoundException | JsonProcessingException exception) {
            return QueryResponse.builder()
                    .message("Error")
                    .status(500)
                    .build();
        }
    }
}

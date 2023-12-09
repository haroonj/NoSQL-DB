package com.example.node.Queries.create;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.FileStorageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateDatabaseQuery implements Query {

    private final FileStorageUtil fileStorageUtil;

    public CreateDatabaseQuery() {
        this.fileStorageUtil = ApplicationContextProvider.getApplicationContext().getBean(FileStorageUtil.class);

    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            boolean created = fileStorageUtil.createDataBase(queryRequest.getDatabase());
            if (created) {
                return QueryResponse.builder()
                        .message("Create database with name " + queryRequest.getDatabase())
                        .status(201)
                        .build();
            }
            return QueryResponse.builder()
                    .message("The database already exists")
                    .status(302)
                    .build();
        } catch (RuntimeException exception) {
            return QueryResponse.builder()
                    .message("Internal Error occurred")
                    .status(500)
                    .build();
        }
    }
}
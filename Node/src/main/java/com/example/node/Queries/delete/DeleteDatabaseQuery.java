package com.example.node.Queries.delete;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.FileStorageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteDatabaseQuery implements Query {
    private final FileStorageUtil fileStorageUtil;

    public DeleteDatabaseQuery() {
        this.fileStorageUtil = ApplicationContextProvider.getApplicationContext().getBean(FileStorageUtil.class);
    }


    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            fileStorageUtil.deleteDatabase(queryRequest.getDatabase());
            return QueryResponse.builder()
                    .message("Database deleted Successfully")
                    .status(200)
                    .build();
        } catch (RuntimeException exception) {
            return QueryResponse.builder()
                    .message("Internal Error occurred")
                    .status(500)
                    .build();
        }
    }
}
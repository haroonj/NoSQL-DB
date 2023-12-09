package com.example.node.Queries.delete;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.IndexUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;

@Slf4j
public class DeleteIndexQuery implements Query {

    private final IndexUtil indexUtil;

    public DeleteIndexQuery() {
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }


    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            indexUtil.deleteIndex(queryRequest.getDatabase(), queryRequest.getCollection(), queryRequest.getBody().get("index").toString());
            return QueryResponse.builder()
                    .message("Index deleted Successfully")
                    .status(200)
                    .build();
        } catch (FileNotFoundException e) {
            return QueryResponse.builder()
                    .message("Index not found")
                    .status(404)
                    .build();
        }
    }
}

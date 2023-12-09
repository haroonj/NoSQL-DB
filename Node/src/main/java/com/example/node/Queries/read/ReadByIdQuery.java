package com.example.node.Queries.read;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.DocumentUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.NoSuchElementException;

@Slf4j
public class ReadByIdQuery implements Query {
    private final DocumentUtil documentUtil;

    public ReadByIdQuery() {
        this.documentUtil = ApplicationContextProvider.getApplicationContext().getBean(DocumentUtil.class);
    }


    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            JSONObject document = documentUtil.getDocumentByFileName(queryRequest.getDatabase(), queryRequest.getCollection(), queryRequest.getBody().get("id").toString());
            return QueryResponse.builder()
                    .message("Here is your document chief")
                    .jsonObject(document.toMap())
                    .status(200)
                    .build();
        } catch (NoSuchElementException exception) {
            return QueryResponse.builder()
                    .message("You missed up chief, the document not found")
                    .status(404)
                    .build();
        }
    }
}
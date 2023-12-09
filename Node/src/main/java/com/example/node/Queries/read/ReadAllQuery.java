package com.example.node.Queries.read;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.DocumentUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.List;
import java.util.NoSuchElementException;
@Slf4j
public class ReadAllQuery implements Query {
    private final DocumentUtil documentUtil;

    public ReadAllQuery() {
        this.documentUtil = ApplicationContextProvider.getApplicationContext().getBean(DocumentUtil.class);

    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            List<JSONObject> documents = documentUtil.getAllDocuments(queryRequest.getDatabase(), queryRequest.getCollection());

            JSONObject combined = new JSONObject();
            for (int i = 0; i < documents.size(); i++) {
                combined.put("document" + i, documents.get(i));
            }
            log.info(combined.toString());
            return QueryResponse.builder()
                    .message("Here is your document chief")
                    .jsonObject(combined.toMap())
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

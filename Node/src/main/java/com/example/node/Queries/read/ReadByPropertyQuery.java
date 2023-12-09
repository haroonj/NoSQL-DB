package com.example.node.Queries.read;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.CollectionUtil;
import com.example.node.util.database.DocumentUtil;
import com.example.node.util.database.IndexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class ReadByPropertyQuery implements Query {
    private final DocumentUtil documentUtil;
    private final CollectionUtil collectionUtil;
    private final IndexUtil indexUtil;

    public ReadByPropertyQuery() {
        this.documentUtil = ApplicationContextProvider.getApplicationContext().getBean(DocumentUtil.class);
        this.collectionUtil = ApplicationContextProvider.getApplicationContext().getBean(CollectionUtil.class);
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }


    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(queryRequest.getDatabase(), queryRequest.getCollection());
            List<JSONObject> documents;
            if (collectionMetaData.getIndexedProperties().contains(queryRequest.getBody().get("property").toString())) {
                documents = indexUtil.getDocumentByProperty(collectionMetaData, queryRequest.getBody().get("property").toString(), queryRequest.getBody().get("value").toString());
            } else {
                documents = documentUtil.getDocumentByProperty(collectionMetaData, queryRequest.getBody().get("property").toString(), queryRequest.getBody().get("value").toString());
            }

            JSONObject combined = new JSONObject();
            for (int i = 0; i < documents.size(); i++) {
                combined.put("document" + i, documents.get(i));
            }

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
        } catch (JsonProcessingException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
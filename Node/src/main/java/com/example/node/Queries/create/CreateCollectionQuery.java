package com.example.node.Queries.create;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.CollectionUtil;
import com.example.node.util.database.IndexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;

@Slf4j
public class CreateCollectionQuery implements Query {
    private final CollectionUtil collectionUtil;
    private final IndexUtil indexUtil;

    public CreateCollectionQuery() {
        this.collectionUtil = ApplicationContextProvider.getApplicationContext().getBean(CollectionUtil.class);
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            CollectionMetaData collectionMetaData = CollectionMetaData.builder()
                    .databaseName(queryRequest.getDatabase())
                    .collectionName(queryRequest.getCollection())
                    .indexedProperties(new ArrayList<>())
                    .build();
            if (collectionUtil.createCollectionMetaDataIfNotExist(collectionMetaData)) {
                collectionUtil.saveCollectionSchema(collectionMetaData, new JSONObject(queryRequest.getBody()).toString());
                indexUtil.createIdIndex(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName());
                return QueryResponse.builder()
                        .message("Create collection with name " + queryRequest.getCollection() + " in database name " + queryRequest.getDatabase())
                        .status(201)
                        .build();
            }
            return QueryResponse.builder()
                    .message("The collection already exists")
                    .status(302)
                    .build();
        } catch (JsonProcessingException exception) {
            return QueryResponse.builder()
                    .message("An Error occurred while creating the collection")
                    .status(500)
                    .build();
        }
    }
}

package com.example.node.Queries.create;

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

import org.everit.json.schema.ValidationException;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Slf4j
public class CreateDocumentQuery implements Query {
    private final CollectionUtil collectionUtil;
    private final DocumentUtil documentUtil;
    private final IndexUtil indexUtil;

    public CreateDocumentQuery() {
        this.collectionUtil = ApplicationContextProvider.getApplicationContext().getBean(CollectionUtil.class);
        this.documentUtil = ApplicationContextProvider.getApplicationContext().getBean(DocumentUtil.class);
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(queryRequest.getDatabase(), queryRequest.getCollection());

            JSONObject body = new JSONObject(queryRequest.getBody());
            long documentId = collectionMetaData.getLastId();
            body.put("_id", collectionMetaData.getDatabaseName() + collectionMetaData.getCollectionName() + documentId);
            collectionUtil.validateDocumentToSchema(collectionMetaData, body);
            Path documentPath = documentUtil.saveDocument(collectionMetaData, body);
            collectionUtil.addCollectionMetaDataDocument(collectionMetaData);
            indexUtil.indexNewDocument(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), body, documentPath);
            return QueryResponse.builder()
                    .message("Create a document in collection name " + queryRequest.getCollection() + "in database name " + queryRequest.getDatabase())
                    .jsonObject(body.toMap())
                    .status(201)
                    .build();
        } catch (JsonProcessingException exception) {
            return QueryResponse.builder()
                    .message("Your document is not good man")
                    .status(500)
                    .build();
        } catch (ValidationException exception) {
            return QueryResponse.builder()
                    .message(exception.getMessage())
                    .status(400)
                    .build();
        } catch (FileNotFoundException | NullPointerException e) {
            return QueryResponse.builder()
                    .message("The collection doesn't have schema")
                    .status(404)
                    .build();
        }
    }
}

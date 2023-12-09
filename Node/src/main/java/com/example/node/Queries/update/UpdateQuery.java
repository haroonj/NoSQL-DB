package com.example.node.Queries.update;

import com.example.node.Queries.Query;
import com.example.node.config.ApplicationContextProvider;
import com.example.node.exceptions.OptimisticLockingFailureException;
import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.util.database.CollectionUtil;
import com.example.node.util.database.DocumentUtil;
import com.example.node.util.database.IndexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Slf4j
public class UpdateQuery implements Query {

    private final CollectionUtil collectionUtil;
    private final DocumentUtil documentUtil;
    private final IndexUtil indexUtil;

    public UpdateQuery() {
        this.collectionUtil = ApplicationContextProvider.getApplicationContext().getBean(CollectionUtil.class);
        this.documentUtil = ApplicationContextProvider.getApplicationContext().getBean(DocumentUtil.class);
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(queryRequest.getDatabase(), queryRequest.getCollection());
            Path path = documentUtil.updateDocument(collectionMetaData, queryRequest.getBody());
            indexUtil.updateDocument(collectionMetaData, queryRequest.getBody(), path);
            return QueryResponse.builder()
                    .message("updated a document in collection name " + queryRequest.getCollection() + "in database name " + queryRequest.getDatabase())
                    .status(200)
                    .jsonObject(documentUtil.getDocumentByAbsolutePath(path.toString()).toMap())
                    .build();
        } catch (JsonProcessingException | FileNotFoundException e) {
            return QueryResponse.builder()
                    .message("Your document is not good man")
                    .status(500)
                    .build();
        } catch (OptimisticLockingFailureException exception) {
            return QueryResponse.builder()
                    .message("Failed to Update the document due to data conflict")
                    .status(400)
                    .jsonObject(queryRequest.getBody())
                    .build();
        }
    }
}
package com.example.node.Queries.delete;

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
import java.util.NoSuchElementException;

@Slf4j
public class DeleteDocumentQuery implements Query {
    private final CollectionUtil collectionUtil;
    private final DocumentUtil documentUtil;
    private final IndexUtil indexUtil;

    public DeleteDocumentQuery() {
        this.collectionUtil = ApplicationContextProvider.getApplicationContext().getBean(CollectionUtil.class);
        this.documentUtil = ApplicationContextProvider.getApplicationContext().getBean(DocumentUtil.class);
        this.indexUtil = ApplicationContextProvider.getApplicationContext().getBean(IndexUtil.class);
    }


    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(queryRequest.getDatabase(), queryRequest.getCollection());
            JSONObject document = documentUtil.getDocumentByFileName(queryRequest.getDatabase(), queryRequest.getCollection(), queryRequest.getBody().get("id").toString());
            documentUtil.deleteDocument(collectionMetaData, queryRequest.getBody().get("id").toString());
            indexUtil.deleteIndexedDocument(collectionMetaData, document);
            collectionUtil.deleteCollectionMetaDataDocument(collectionMetaData);
            return QueryResponse.builder()
                    .message("Document deleted Successfully")
                    .status(200)
                    .build();
        } catch (JsonProcessingException | FileNotFoundException exception) {
            log.error(exception.getMessage());
            return QueryResponse.builder()
                    .message("Document not deleted Successfully")
                    .status(500)
                    .build();
        } catch (NoSuchElementException exception) {
            return QueryResponse.builder()
                    .message("Document not found")
                    .status(404)
                    .build();
        }
    }
}

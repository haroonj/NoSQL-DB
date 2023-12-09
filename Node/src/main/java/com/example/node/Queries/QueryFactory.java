package com.example.node.Queries;

import com.example.node.Queries.create.CreateCollectionQuery;
import com.example.node.Queries.create.CreateDatabaseQuery;
import com.example.node.Queries.create.CreateDocumentQuery;
import com.example.node.Queries.create.CreateIndexQuery;
import com.example.node.Queries.delete.DeleteCollectionQuery;
import com.example.node.Queries.delete.DeleteDatabaseQuery;
import com.example.node.Queries.delete.DeleteDocumentQuery;
import com.example.node.Queries.delete.DeleteIndexQuery;
import com.example.node.Queries.read.ReadAllQuery;
import com.example.node.Queries.read.ReadByIdQuery;
import com.example.node.Queries.read.ReadByPropertyQuery;
import com.example.node.Queries.update.UpdateQuery;
import com.example.node.model.request.QueryRequest;

public class QueryFactory {

    public Query makeQuery(QueryRequest queryRequest) {
        switch (queryRequest.getOperation()) {
            case CREATE_DATABASE:
                return new CreateDatabaseQuery();
            case CREATE_COLLECTION:
                return new CreateCollectionQuery();
            case CREATE_INDEX:
                return new CreateIndexQuery();
            case CREATE_DOCUMENT:
                return new CreateDocumentQuery();
            case READ_BY_ID:
                return new ReadByIdQuery();
            case READ_ALL:
                return new ReadAllQuery();
            case READ_DOCUMENT_BY_PROPERTY:
                return new ReadByPropertyQuery();
            case UPDATE:
                return new UpdateQuery();
            case DELETE_DATABASE:
                return new DeleteDatabaseQuery();
            case DELETE_COLLECTION:
                return new DeleteCollectionQuery();
            case DELETE_INDEX:
                return new DeleteIndexQuery();
            case DELETE_DOCUMENT:
                return new DeleteDocumentQuery();
            default:
                return new InvalidQuery();
        }
    }

}
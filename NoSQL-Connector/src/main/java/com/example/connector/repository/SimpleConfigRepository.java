package com.example.connector.repository;

import com.example.connector.connection.NoSQLDatabaseConnection;
import com.example.connector.model.query.OperationType;
import com.example.connector.model.request.QueryRequest;
import com.example.connector.model.response.QueryResponse;
import org.json.JSONObject;

class SimpleConfigRepository implements ConfigRepository {

    private final NoSQLDatabaseConnection connection;

    SimpleConfigRepository(NoSQLDatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public boolean createDatabase() {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_DATABASE)
                        .database(connection.getConfig().getDatabase())
                        .build()
        );
        return response.getStatus() == 201 || response.getStatus() == 302;
    }

    @Override
    public boolean createCollection(String collectionName, JSONObject schema) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_COLLECTION)
                        .database(connection.getConfig().getDatabase())
                        .collection(collectionName)
                        .body(schema.toMap())
                        .build()
        );
        return response.getStatus() == 201 || response.getStatus() == 302;
    }

    @Override
    public boolean deleteDatabase() {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.DELETE_DATABASE)
                        .database(connection.getConfig().getDatabase())
                        .build()
        );
        return response.getStatus() == 200;
    }

    @Override
    public boolean deleteCollection(String collectionName) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.DELETE_COLLECTION)
                        .database(connection.getConfig().getDatabase())
                        .collection(collectionName)
                        .build()
        );
        return response.getStatus() == 200;
    }
}

package com.example.node.config;


import com.example.node.Queries.create.CreateCollectionQuery;
import com.example.node.Queries.create.CreateDatabaseQuery;
import com.example.node.Queries.create.CreateIndexQuery;
import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.system.Affinity;
import com.example.node.model.system.Node;
import com.example.node.model.system.User;
import com.example.node.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        initiateNodes();
        initiateAffinities();
        initiateUsers();
    }

    private void initiateNodes() {
        new CreateDatabaseQuery().performQuery(QueryRequest.builder()
                .operation(OperationType.CREATE_DATABASE)
                .database("System")
                .build());
        boolean isNewCollection = new CreateCollectionQuery().performQuery(QueryRequest.builder()
                .operation(OperationType.CREATE_COLLECTION)
                .database("System")
                .collection("Node")
                .body(new JSONObject(JSONUtil.generateJsonSchema(Node.class)).toMap())
                .build()).getStatus() == 201;
        log.info(String.valueOf(isNewCollection));
        if (isNewCollection) {
            new CreateIndexQuery().performQuery(
                    QueryRequest.builder()
                            .operation(OperationType.CREATE_INDEX)
                            .database("System")
                            .collection("Node")
                            .body(new JSONObject()
                                    .put("indexedBy", "name")
                                    .toMap())
                            .build()
            );
            new CreateIndexQuery().performQuery(
                    QueryRequest.builder()
                            .operation(OperationType.CREATE_INDEX)
                            .database("System")
                            .collection("Node")
                            .body(new JSONObject()
                                    .put("indexedBy", "url")
                                    .toMap())
                            .build()
            );
        }
    }

    private void initiateAffinities() {
        new CreateDatabaseQuery().performQuery(QueryRequest.builder()
                .operation(OperationType.CREATE_DATABASE)
                .database("System")
                .build());
        boolean isNewCollection = new CreateCollectionQuery().performQuery(QueryRequest.builder()
                .operation(OperationType.CREATE_COLLECTION)
                .database("System")
                .collection("Affinity")
                .body(new JSONObject(JSONUtil.generateJsonSchema(Affinity.class)).toMap())
                .build()).getStatus() == 201;
        if (isNewCollection) {
            new CreateIndexQuery().performQuery(
                    QueryRequest.builder()
                            .operation(OperationType.CREATE_INDEX)
                            .database("System")
                            .collection("Affinity")
                            .body(new JSONObject()
                                    .put("indexedBy", "_nodeId")
                                    .toMap())
                            .build()
            );
            new CreateIndexQuery().performQuery(
                    QueryRequest.builder()
                            .operation(OperationType.CREATE_INDEX)
                            .database("System")
                            .collection("Affinity")
                            .body(new JSONObject()
                                    .put("indexedBy", "_documentId")
                                    .toMap())
                            .build()
            );
        }
    }

    private void initiateUsers() {
        new CreateDatabaseQuery().performQuery(QueryRequest.builder()
                .operation(OperationType.CREATE_DATABASE)
                .database("System")
                .build());
        boolean isNewCollection = new CreateCollectionQuery().performQuery(QueryRequest.builder()
                .operation(OperationType.CREATE_COLLECTION)
                .database("System")
                .collection("User")
                .body(new JSONObject(JSONUtil.generateJsonSchema(User.class)).toMap())
                .build()).getStatus() == 201;
        if (isNewCollection) {
            new CreateIndexQuery().performQuery(
                    QueryRequest.builder()
                            .operation(OperationType.CREATE_INDEX)
                            .database("System")
                            .collection("User")
                            .body(new JSONObject()
                                    .put("indexedBy", "username")
                                    .toMap())
                            .build()
            );
        }
    }
}

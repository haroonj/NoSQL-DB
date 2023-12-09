package com.example.bootstrapping.repository.config;


import com.example.bootstrapping.model.query.OperationType;
import com.example.bootstrapping.model.query.UpdateDocumentQuery;
import com.example.bootstrapping.model.request.QueryRequest;
import com.example.bootstrapping.model.response.QueryResponse;
import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.service.CommunicationService;
import com.example.bootstrapping.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@SuppressWarnings("unchecked")
public abstract class CRUDNoSQLRepository<Entity, ID> implements NoSQLRepository<Entity, ID> {

    private final Class<Entity> entityType;

    public CRUDNoSQLRepository() {
        Type superclass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        this.entityType = (Class<Entity>) type;
    }

    @Override
    public boolean createDatabase(CommunicationService communicationService, Node node, String token) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.CREATE_DATABASE)
                        .database("System")
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 201 || response.getStatus() == 302;
    }

    @Override
    public boolean createCollection(CommunicationService communicationService, Node node, String token) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.CREATE_COLLECTION)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body(new JSONObject(JSONUtil.generateJsonSchema(entityType)).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 201 || response.getStatus() == 302;
    }

    @Override
    public boolean deleteDatabase(CommunicationService communicationService, Node node, String token) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.DELETE_DATABASE)
                        .database("System")
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 200;
    }

    @Override
    public boolean deleteCollection(CommunicationService communicationService, Node node, String token) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.DELETE_COLLECTION)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 200;
    }

    @Override
    public Entity createDocument(CommunicationService communicationService, Node node, Entity entity, String token) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body(new JSONObject(entity).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return JSONUtil.parseObject(new JSONObject(response.getJsonObject()), entityType);
    }

    @Override
    public boolean createIndex(CommunicationService communicationService, Node node, String token, String index) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.CREATE_INDEX)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("indexedBy", index)).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 201;
    }

    @Override
    public Entity getDocumentByID(CommunicationService communicationService, Node node, String token, ID id) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.READ_BY_ID)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("id", id)).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return JSONUtil.parseObject(new JSONObject(response.getJsonObject()), entityType);
    }

    @Override
    public List<Entity> getAllDocuments(CommunicationService communicationService, Node node, String token) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.READ_ALL)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        log.info(response.toString());
        return JSONUtil.parseJsonToList(new JSONObject(response.getJsonObject()).toString(), entityType);
    }

    @Override
    public List<Entity> getAllDocumentsByProperty(CommunicationService communicationService, Node node, String token, String property, String value) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.READ_DOCUMENT_BY_PROPERTY)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject()
                                .put("property", property)
                                .put("value", value)
                        ).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return JSONUtil.parseJsonToList(new JSONObject(response.getJsonObject()).toString(), entityType);
    }

    @Override
    public Entity updateDocument(CommunicationService communicationService, Node node, String token, UpdateDocumentQuery updateDocumentQuery) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.UPDATE)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject(updateDocumentQuery)).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return JSONUtil.parseObject(new JSONObject(response.getJsonObject()), entityType);
    }

    @Override
    public boolean deleteIndex(CommunicationService communicationService, Node node, String token, String index) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.DELETE_INDEX)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("index", index)).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 200;
    }

    @Override
    public boolean deleteDocumentById(CommunicationService communicationService, Node node, String token, ID id) {
        QueryResponse response = communicationService.sendPostRequest(
                node
                , QueryRequest.builder()
                        .operation(OperationType.DELETE_DOCUMENT)
                        .database("System")
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("id", id)).toMap())
                        .build()
                , "/query/processQuery"
                , token
        ).block();
        assert response != null;
        return response.getStatus() == 200;
    }
}

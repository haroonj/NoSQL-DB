package com.example.connector.repository;

import com.example.connector.connection.NoSQLDatabaseConnection;
import com.example.connector.exception.NoSQLConnectionException;
import com.example.connector.model.query.OperationType;
import com.example.connector.model.query.UpdateDocumentQuery;
import com.example.connector.model.request.QueryRequest;
import com.example.connector.model.response.QueryResponse;
import com.example.connector.utils.JSONUtil;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class CRUDNoSQLRepository<Entity, ID> implements NoSQLRepository<Entity, ID> {

    private final ConfigRepository configRepository;
    private final NoSQLDatabaseConnection connection;
    private final Class<Entity> entityType;

    public CRUDNoSQLRepository() {
        Type superclass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        this.entityType = (Class<Entity>) type;
        this.connection = NoSQLDatabaseConnection.getInstance();
        this.configRepository = new SimpleConfigRepository(this.connection);
        init();
    }

    private void init() {
        if (configRepository.createDatabase()) {
            if (configRepository.createCollection(entityType.getSimpleName(), new JSONObject(JSONUtil.generateJsonSchema(entityType)))) {
                return;
            }
        }
        throw new NoSQLConnectionException("NoSQLConnectionException");
    }

    @Override
    public Entity createDocument(Entity entity) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body(new JSONObject(entity).toMap())
                        .build()
        );
        return JSONUtil.parseObject(new JSONObject(response.getJsonObject()), entityType);
    }

    @Override
    public boolean createIndex(String index) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_INDEX)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("indexedBy", index)).toMap())
                        .build()
        );
        return response.getStatus() == 201;
    }

    @Override
    public Entity getDocumentByID(ID id) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.READ_BY_ID)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("id", id)).toMap())
                        .build()
        );
        return JSONUtil.parseObject(new JSONObject(response.getJsonObject()), entityType);
    }

    @Override
    public List<Entity> getAllDocuments() {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.READ_ALL)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .build()
        );
        return JSONUtil.parseJsonToList(new JSONObject(response.getJsonObject()).toString(), entityType);
    }

    @Override
    public List<Entity> getAllDocumentsByProperty(String property, String value) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.READ_DOCUMENT_BY_PROPERTY)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject()
                                .put("property", property)
                                .put("value", value)
                        ).toMap())
                        .build()
        );
        return JSONUtil.parseJsonToList(new JSONObject(response.getJsonObject()).toString(), entityType);
    }

    @Override
    public Entity updateDocument(UpdateDocumentQuery updateDocumentQuery) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.UPDATE)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject(updateDocumentQuery)).toMap())
                        .build()
        );
        return JSONUtil.parseObject(new JSONObject(response.getJsonObject()), entityType);
    }

    @Override
    public boolean deleteIndex(String index) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.DELETE_INDEX)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("index", index)).toMap())
                        .build()
        );
        return response.getStatus() == 200;
    }

    @Override
    public boolean deleteDocumentById(ID id) {
        QueryResponse response = connection.post(
                QueryRequest.builder()
                        .operation(OperationType.DELETE_DOCUMENT)
                        .database(connection.getConfig().getDatabase())
                        .collection(entityType.getSimpleName())
                        .body((new JSONObject().put("id", id)).toMap())
                        .build()
        );
        return response.getStatus() == 200;
    }
}

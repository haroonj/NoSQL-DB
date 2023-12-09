package com.example.node.util.system;

import com.example.node.Queries.create.CreateDocumentQuery;
import com.example.node.Queries.delete.DeleteDocumentQuery;
import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.model.system.Affinity;
import com.example.node.util.database.IndexUtil;
import com.example.node.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AffinityUtil {
    private final IndexUtil indexUtil;
    private final NodeUtil nodeUtil;

    public AffinityUtil(IndexUtil indexUtil, NodeUtil nodeUtil) {
        this.indexUtil = indexUtil;
        this.nodeUtil = nodeUtil;
    }

    public String findAffinityNode(String documentId) {
        Affinity affinity = JSONUtil.parseObject(
                indexUtil.getDocumentByUniqueProperty(CollectionMetaData.builder()
                                .databaseName("System")
                                .collectionName("Affinity")
                                .build()
                        , "_documentId", documentId), Affinity.class);
        return affinity.get_nodeId();
    }

    public QueryResponse addAffinity(QueryRequest queryRequest) {
        new CreateDocumentQuery()
                .performQuery(QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database("System")
                        .collection("Affinity")
                        .body(new JSONObject(JSONUtil.parseObject(new JSONObject(queryRequest.getBody()), Affinity.class)).toMap())
                        .build());
        nodeUtil.addAffinity(JSONUtil.parseObject(new JSONObject(queryRequest.getBody()), Affinity.class));
        return QueryResponse.builder()
                .status(201)
                .message("Affinity added")
                .jsonObject((new JSONObject().put("numOfRecords", 1)).toMap())
                .build();
    }

    public QueryResponse deleteAffinityDocument(QueryRequest queryRequest) {
        JSONObject affinity = indexUtil.getDocumentByUniqueProperty(CollectionMetaData.builder()
                .databaseName("System")
                .collectionName("Affinity")
                .build(), "_documentId", queryRequest.getBody().get("id").toString());
        new DeleteDocumentQuery()
                .performQuery(QueryRequest.builder()
                        .operation(OperationType.DELETE_DOCUMENT)
                        .database("System")
                        .collection("Affinity")
                        .body((new JSONObject().put("id", affinity.get("_id"))).toMap())
                        .build());
        nodeUtil.removeAffinity(JSONUtil.parseObject(affinity, Affinity.class));
        return QueryResponse.builder()
                .status(201)
                .message("Affinity deleted")
                .jsonObject((new JSONObject().put("numOfRecords", -1)).toMap())
                .build();
    }

    public QueryResponse deleteAffinitiesByDatabase(QueryRequest queryRequest) {
        List<Affinity> affinities = indexUtil.deleteAffinitiesByContaining(queryRequest.getDatabase());
        nodeUtil.removeAffinities(affinities);
        return QueryResponse.builder()
                .status(200)
                .message("Affinities deleted")
                .jsonObject((new JSONObject().put("numOfRecords", -1 * affinities.size())).toMap())
                .build();
    }

    public QueryResponse deleteAffinitiesByCollection(QueryRequest queryRequest) {
        List<Affinity> affinities = indexUtil.deleteAffinitiesByContaining(queryRequest.getDatabase() + queryRequest.getCollection());
        nodeUtil.removeAffinities(affinities);
        return QueryResponse.builder()
                .status(200)
                .message("Affinities deleted")
                .jsonObject((new JSONObject().put("numOfRecords", -1 * affinities.size())).toMap())
                .build();
    }


    public QueryResponse reflectAffinity(QueryRequest queryRequest) {
        QueryResponse queryResponse;
        switch (queryRequest.getOperation()) {
            case CREATE_DOCUMENT:
                queryResponse = addAffinity(queryRequest);
                break;
            case DELETE_DOCUMENT:
                queryResponse = deleteAffinityDocument(queryRequest);
                break;
            case DELETE_DATABASE:
                queryResponse = deleteAffinitiesByDatabase(queryRequest);
                break;
            case DELETE_COLLECTION:
                queryResponse = deleteAffinitiesByCollection(queryRequest);
                break;
            default:
                queryResponse = new QueryResponse();
        }
        return queryResponse;
    }
}

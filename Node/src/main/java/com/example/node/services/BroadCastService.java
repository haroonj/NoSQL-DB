package com.example.node.services;

import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.model.system.Affinity;
import com.example.node.model.system.Node;
import com.example.node.util.system.AffinityUtil;
import com.example.node.util.system.NodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BroadCastService {
    private final CommunicationService communicationService;
    private final NodeUtil nodeUtil;
    private final AffinityUtil affinityUtil;

    public BroadCastService(CommunicationService communicationService, NodeUtil nodeUtil, AffinityUtil affinityUtil) {
        this.nodeUtil = nodeUtil;
        this.affinityUtil = affinityUtil;
        this.communicationService = communicationService;
    }

    public QueryResponse forwardRequest(Node node, QueryRequest body) {
        log.info("forwarding request to " + node.getUrl() + "\n with request body = " + body);
        return communicationService.sendPostRequest(node.getUrl(), body, "processForwardedQuery").block();
    }

    public void broadCastChanges(QueryResponse queryResponse, QueryRequest queryRequest) {
        if ((queryResponse.getStatus() == 200 || queryResponse.getStatus() == 201)) {
            List<Node> nodeList = nodeUtil.getAllOtherNodes();
            communicationService.sendPostRequestsToMultipleUrls(nodeList, queryRequest, "processQuery").collectList().block();
            log.info(String.valueOf(queryResponse));
            broadCastAffinities(queryResponse, queryRequest, nodeList);
        }
    }

    public QueryResponse reflectAffinity(QueryRequest queryRequest) {
        log.info("reflectAffinity ");
        return affinityUtil.reflectAffinity(queryRequest);
    }

    private void broadCastAffinities(QueryResponse queryResponse, QueryRequest queryRequest, List<Node> nodeList) {
        if (queryRequest.getOperation().equals(OperationType.CREATE_DOCUMENT)) {
            queryRequest.setBody(new JSONObject(Affinity.builder()
                    ._documentId(queryResponse.getJsonObject().get("_id").toString())
                    ._nodeId(nodeUtil.getNode().get_id())
                    .build()).toMap());
            broadCastAffinity(queryRequest, nodeList);
        } else {
            if (queryRequest.getOperation().getCategory().equals(OperationType.Category.DELETE) && !queryRequest.getOperation().equals(OperationType.DELETE_INDEX)) {
                broadCastAffinity(queryRequest, nodeList);
            }
        }
    }

    private void broadCastAffinity(QueryRequest queryRequest, List<Node> nodeList) {
        reflectAffinity(queryRequest);
        log.info("broadCastAffinity ");
        List<QueryResponse> queryResponse = communicationService.sendPostRequestsToMultipleUrls(
                nodeList
                , queryRequest
                , "reflectAffinity"
        ).collectList().block();
        log.info(String.valueOf(queryResponse));
    }
}

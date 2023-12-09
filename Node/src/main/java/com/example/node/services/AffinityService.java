package com.example.node.services;

import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.model.system.Node;
import com.example.node.util.system.AffinityUtil;
import com.example.node.util.system.NodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AffinityService {
    private final NodeUtil nodeUtil;
    private final AffinityUtil affinityUtil;
    private final QueryService queryService;

    private final BroadCastService broadCastService;

    public AffinityService(NodeUtil nodeUtil, AffinityUtil affinityUtil, QueryService queryService, BroadCastService broadCastService) {
        this.nodeUtil = nodeUtil;
        this.affinityUtil = affinityUtil;
        this.queryService = queryService;
        this.broadCastService = broadCastService;
    }

    public QueryResponse processAffinity(QueryRequest queryRequest) {
        Node affinityNode;
        QueryResponse queryResponse;
        if (OperationType.hasAffinity(queryRequest.getOperation())) {
            log.info("has affinity ");
            String documentId = queryRequest.getBody().get("id").toString();
            String nodeId = affinityUtil.findAffinityNode(documentId);
            affinityNode = nodeUtil.getNodeByNodeId(nodeId);
        } else {
            affinityNode = nodeUtil.findBalancedNode();
        }
        log.info("affinityNode -> " + affinityNode);
        if (nodeUtil.isInLocalNode(affinityNode)) {
            log.info("is local node ");
            queryResponse = queryService.processQuery(queryRequest);
            broadCastService.broadCastChanges(queryResponse, queryRequest);
        } else {
            log.info("forwarding to node -> " + affinityNode);
            queryResponse = broadCastService.forwardRequest(affinityNode, queryRequest);
        }

        return queryResponse;
    }
}

package com.example.node.util.system;

import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.system.Affinity;
import com.example.node.model.system.Node;
import com.example.node.util.database.DocumentUtil;
import com.example.node.util.database.IndexUtil;
import com.example.node.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@PropertySource("classpath:application.properties")
public class NodeUtil {
    private final IndexUtil indexUtil;
    private final DocumentUtil documentUtil;

    @Value("${NoSQL.node.url}")
    private String nodeUrl;
    private Node thisNode;

    public NodeUtil(IndexUtil indexUtil, DocumentUtil documentUtil) {
        this.indexUtil = indexUtil;
        this.documentUtil = documentUtil;
    }


    public Node getNode() {
        this.thisNode = JSONUtil.parseObject(
                indexUtil.getDocumentByUniqueProperty(CollectionMetaData.builder()
                                .databaseName("System")
                                .collectionName("Node")
                                .build()
                        , "url", nodeUrl), Node.class);
        return this.thisNode;
    }

    public Node getNodeByNodeId(String nodeId) {
        return JSONUtil.parseObject(
                indexUtil.getDocumentByUniqueProperty(CollectionMetaData.builder()
                                .databaseName("System")
                                .collectionName("Node")
                                .build()
                        , "_id", nodeId), Node.class);
    }

    public boolean isInLocalNode(Node node) {
        getNode();
        return this.thisNode.get_id().equals(node.get_id());
    }


    public Node findBalancedNode() {
        getNode();
        List<Node> nodeList = getAllNodes();
        return nodeList.stream()
                .min(Comparator.comparingLong(Node::getNumOfAffinities))
                .orElse(this.thisNode);
    }

    public List<Node> getAllOtherNodes() {
        log.info(getNode().toString());
        List<Node> nodeList = getAllNodes();
        nodeList.remove(this.thisNode);
        return nodeList;
    }

    private List<Node> getAllNodes() {
        return JSONUtil.parseJsonListToList(documentUtil.getAllDocuments("System", "Node"), Node.class);
    }

    public void addAffinity(Affinity affinity) {
        Node affinityNode = getNodeByNodeId(affinity.get_nodeId());
        affinityNode.addAffinity();
        saveNode(affinityNode);
    }

    public void removeAffinity(Affinity affinity) {
        Node affinityNode = getNodeByNodeId(affinity.get_nodeId());
        affinityNode.removeAffinity();
        saveNode(affinityNode);
    }

    public void removeAffinities(List<Affinity> affinities) {
        for (Affinity affinity : affinities) {
            removeAffinity(affinity);
        }
    }

    private void saveNode(Node node) {
        CollectionMetaData collectionMetaData = CollectionMetaData.builder()
                .databaseName("System")
                .collectionName("Node")
                .build();
        documentUtil.saveDocument(collectionMetaData, new JSONObject(node));
    }
}

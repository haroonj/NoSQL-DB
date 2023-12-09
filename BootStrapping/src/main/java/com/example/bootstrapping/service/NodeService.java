package com.example.bootstrapping.service;

import com.example.bootstrapping.model.query.UpdateDocumentQuery;
import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.repository.NodeRepository;
import com.example.bootstrapping.util.AdminTokenGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class NodeService {
    private final CommunicationService communicationService;
    private final AdminTokenGenerator adminTokenGenerator;
    NodeRepository nodeRepository = new NodeRepository();
    private boolean isReady;
    private List<Node> cachedNodes;

    public NodeService(CommunicationService communicationService, AdminTokenGenerator adminTokenGenerator) {
        this.communicationService = communicationService;
        this.adminTokenGenerator = adminTokenGenerator;
        cachedNodes = new ArrayList<>();
        isReady = false;
    }

    public Node getNode() {
        return cachedNodes.stream()
                .min(Comparator.comparingLong(Node::getNumOfUsers))
                .orElse(null);
    }

    public void addUserToNode(Node loadBalancedNode) {
        cachedNodes.remove(loadBalancedNode);
        loadBalancedNode.addUsers();
        cachedNodes.add(loadBalancedNode);
        String adminToken = adminTokenGenerator.getToken(communicationService, loadBalancedNode);
        UpdateDocumentQuery updateDocumentQuery = UpdateDocumentQuery.builder()
                .id(loadBalancedNode.get_id())
                .property("numOfUsers")
                .oldValue(String.valueOf(loadBalancedNode.getNumOfUsers() - 1))
                .newValue(String.valueOf(loadBalancedNode.getNumOfUsers()))
                .build();
        nodeRepository.updateDocument(communicationService, loadBalancedNode, adminToken, updateDocumentQuery);
    }

    public List<Node> generateNodes(Integer integer) {
        List<Node> generatedNodes = new ArrayList<>();
        for (int i = 0; i < integer; i++) {
            Node node = Node.builder()
                    ._id("SystemNode" + i)
                    .name("node" + i)
                    .port(8000 + i)
                    .build();
            node.setUrl("http://" + node.getName() + ":" + node.getPort());
            generatedNodes.add(node);
        }
        cachedNodes.addAll(generatedNodes);
        return generatedNodes;
    }

    public void removeUserFromNode(String nodeUrl) {
        Optional<Node> userNode = cachedNodes.stream()
                .filter(node -> nodeUrl.equals(node.getUrl()))
                .findFirst();
        if (userNode.isPresent()) {
            Node node = userNode.get();
            cachedNodes.remove(node);
            node.removeUsers();
            cachedNodes.add(node);
            String adminToken = adminTokenGenerator.getToken(communicationService, node);
            UpdateDocumentQuery updateDocumentQuery = UpdateDocumentQuery.builder()
                    .id(node.get_id())
                    .property("numOfUsers")
                    .oldValue(String.valueOf(node.getNumOfUsers() + 1))
                    .newValue(String.valueOf(node.getNumOfUsers()))
                    .build();
            nodeRepository.updateDocument(communicationService, node, adminToken, updateDocumentQuery);
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}

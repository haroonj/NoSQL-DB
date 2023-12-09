package com.example.bootstrapping.config;


import com.example.bootstrapping.model.query.OperationType;
import com.example.bootstrapping.model.request.CreateContainerRequest;
import com.example.bootstrapping.model.request.QueryRequest;
import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.model.system.User;
import com.example.bootstrapping.service.CommunicationService;
import com.example.bootstrapping.service.DockerService;
import com.example.bootstrapping.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AppStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final CommunicationService communicationService;
    private final DockerService dockerService;
    private final NodeService nodeService;

    @Value("${node.number}")
    private String numOfNodes;
    @Value("${node.image}")
    private String nodeImage;
    @Value("${node.network}")
    private String networkName;


    public AppStartupListener(CommunicationService communicationService, DockerService dockerService, NodeService nodeService) {
        this.communicationService = communicationService;
        this.dockerService = dockerService;
        this.nodeService = nodeService;
    }


    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        try {
            List<Node> nodeList = nodeService.generateNodes(Integer.parseInt(numOfNodes));
            log.info(nodeList.toString());
            int initialPort = 8000;
            for (Node node : nodeList) {
                CreateContainerRequest createContainerRequest = new CreateContainerRequest(nodeImage, networkName, node.getName(), node.getUrl(), initialPort++);
                dockerService.createAndStartContainer(createContainerRequest);
            }
            Thread.sleep(20000);
            for (Node node : nodeList) {
                log.info("start node " + node);
                initiateNodesMetaData(node, nodeList);
                log.info("finish node " + node);
            }
            initiateAdminUser(nodeList);
            log.info("finish admin user ");
            nodeService.setReady(true);
        } catch (InterruptedException | IllegalArgumentException e) {
            log.error(e.getMessage());
            nodeService.setReady(false);
            Thread.currentThread().interrupt();
        }
    }

    private void initiateNodesMetaData(Node node, List<Node> nodeList) {
        QueryRequest queryRequest = QueryRequest.builder()
                .operation(OperationType.CREATE_DOCUMENT)
                .database("System")
                .collection("Node")
                .body(new JSONObject(node).toMap())
                .build();
        communicationService.sendPostRequestsToMultipleUrls(nodeList, queryRequest, "/node/register");
    }

    private void initiateAdminUser(List<Node> nodeList) {
        User adminUser = User.builder()
                ._id("SystemNode0")
                .full_name("Super Admin")
                .username("admin")
                .password("admin")
                .role("admin")
                .build();
        QueryRequest queryRequest = QueryRequest.builder()
                .operation(OperationType.CREATE_DOCUMENT)
                .database("System")
                .collection("User")
                .body(new JSONObject(adminUser).toMap())
                .build();
        communicationService.sendPostRequestsToMultipleUrls(nodeList, queryRequest, "/user/register");
    }
}
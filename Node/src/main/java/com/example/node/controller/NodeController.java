package com.example.node.controller;

import com.example.node.Queries.create.CreateDocumentQuery;
import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.model.system.Affinity;
import com.example.node.model.system.Node;
import com.example.node.util.JSONUtil;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("node")
public class NodeController {

    @PostMapping("/register")
    public QueryResponse register(@RequestBody QueryRequest queryRequest) {
        Node node = JSONUtil.parseObject(new JSONObject(queryRequest.getBody()), Node.class);
        QueryResponse nodeResponse = new CreateDocumentQuery().performQuery(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database("System")
                        .collection("Node")
                        .body(new JSONObject(node).toMap())
                        .build()
        );
        Node dbNode = JSONUtil.parseObject(new JSONObject(nodeResponse.getJsonObject()), Node.class);
        Affinity nodeAffinity = Affinity.builder()
                ._nodeId(dbNode.get_id())
                ._documentId(dbNode.get_id())
                .build();

        new CreateDocumentQuery().performQuery(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database("System")
                        .collection("Affinity")
                        .body(new JSONObject(nodeAffinity).toMap())
                        .build()
        );
        return nodeResponse;
    }

    @GetMapping("/test")
    public QueryResponse test() {
        return new QueryResponse();
    }
}

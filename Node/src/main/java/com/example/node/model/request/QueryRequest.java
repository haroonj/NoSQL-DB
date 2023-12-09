package com.example.node.model.request;

import com.example.node.model.query.OperationType;
import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryRequest {
    private OperationType operation;
    private String database;
    private String collection;
    private Map<String, Object> body;
}

package com.example.bootstrapping.model.request;

import com.example.bootstrapping.model.query.OperationType;
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

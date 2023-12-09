package com.example.node.model.response;

import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryResponse {
    private Map<String, Object> jsonObject;
    private String message;
    private int status;
}

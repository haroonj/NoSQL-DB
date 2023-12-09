package com.example.node.Queries;

import com.example.node.model.query.OperationType;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class InvalidQuery implements Query {


    public InvalidQuery() {
    }

    @Override
    public QueryResponse performQuery(QueryRequest queryRequest) {
        Map<Integer, Object> map = IntStream.range(0, OperationType.values().length)
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> OperationType.values()[i]));

        return QueryResponse.builder()
                .message("Not Supported Query")
                .status(400)
                .jsonObject(new JSONObject(map).toMap())
                .build();
    }
}

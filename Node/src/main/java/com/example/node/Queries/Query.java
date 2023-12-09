package com.example.node.Queries;

import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;

public interface Query {

    QueryResponse performQuery(QueryRequest queryRequest);
}

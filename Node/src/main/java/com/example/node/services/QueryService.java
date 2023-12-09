package com.example.node.services;

import com.example.node.Queries.Query;
import com.example.node.Queries.QueryFactory;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    private final QueryFactory queryFactory;

    public QueryService() {
        this.queryFactory = new QueryFactory();
    }

    public QueryResponse processQuery(QueryRequest queryRequest) {
        Query query = queryFactory.makeQuery(queryRequest);
        return query.performQuery(queryRequest);
    }

}

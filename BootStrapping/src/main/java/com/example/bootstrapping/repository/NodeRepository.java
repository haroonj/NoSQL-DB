package com.example.bootstrapping.repository;

import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.repository.config.CRUDNoSQLRepository;
import org.springframework.stereotype.Repository;

@Repository
public class NodeRepository extends CRUDNoSQLRepository<Node, String> {
}

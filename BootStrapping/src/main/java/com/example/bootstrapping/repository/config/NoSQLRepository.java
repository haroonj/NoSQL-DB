package com.example.bootstrapping.repository.config;


import com.example.bootstrapping.model.query.UpdateDocumentQuery;
import com.example.bootstrapping.model.system.Node;
import com.example.bootstrapping.service.CommunicationService;

import java.util.List;

public interface NoSQLRepository<Entity, ID> {
    boolean createDatabase(CommunicationService communicationService, Node node, String token);

    boolean createCollection(CommunicationService communicationService, Node node, String token);

    boolean deleteDatabase(CommunicationService communicationService, Node node, String token);

    boolean deleteCollection(CommunicationService communicationService, Node node, String token);

    Entity createDocument(CommunicationService communicationService, Node node, Entity entity, String token);

    boolean createIndex(CommunicationService communicationService, Node node, String token, String index);

    Entity getDocumentByID(CommunicationService communicationService, Node node, String token,ID id);

    List<Entity> getAllDocuments(CommunicationService communicationService, Node node, String token);

    List<Entity> getAllDocumentsByProperty(CommunicationService communicationService, Node node, String token,String property, String value);

    Entity updateDocument(CommunicationService communicationService, Node node, String token,UpdateDocumentQuery updateDocumentQuery);

    boolean deleteIndex(CommunicationService communicationService, Node node, String token,String index);

    boolean deleteDocumentById(CommunicationService communicationService, Node node, String token,ID id);
}

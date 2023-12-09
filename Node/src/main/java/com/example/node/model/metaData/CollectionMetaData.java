package com.example.node.model.metaData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionMetaData {
    String databaseName;
    String collectionName;
    long numOfDocuments;
    long lastId;
    List<String> indexedProperties;

    public void addIndexedProperty(String property){
        if(indexedProperties == null)
            indexedProperties = new ArrayList<>();
        indexedProperties.add(property);
    }
    public void removeIndexedProperty(String property){
        if(indexedProperties == null)
            indexedProperties = new ArrayList<>();
        indexedProperties.remove(property);
    }
}
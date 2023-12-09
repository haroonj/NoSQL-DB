package com.example.node.util.database;

import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class CollectionUtil {
    private final FileStorageUtil fileStorageUtil;
    private final ObjectMapper objectMapper;
    private final ReentrantReadWriteLock rwLock;


    public CollectionUtil(FileStorageUtil fileStorageUtil) {
        this.fileStorageUtil = fileStorageUtil;
        objectMapper = new ObjectMapper();
        this.rwLock = new ReentrantReadWriteLock();
    }

    public CollectionMetaData getCollectionMetaData(String databaseName, String collectionName) throws FileNotFoundException {
        rwLock.readLock().lock();
        try {
            Optional<JSONObject> optionalJSONObject = fileStorageUtil.getFile(databaseName, collectionName, "collectionMetaData");
            if (optionalJSONObject.isPresent()) {
                return JSONUtil.parseObject(optionalJSONObject.get(), CollectionMetaData.class);
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.toString());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void saveCollectionMetaData(CollectionMetaData collectionMetaData) throws JsonProcessingException {
        rwLock.writeLock().lock();
        try {
            fileStorageUtil.storeFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), "collectionMetaData", objectMapper.writeValueAsString(collectionMetaData));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean createCollectionMetaDataIfNotExist(CollectionMetaData collectionMetaData) throws JsonProcessingException {
        rwLock.writeLock().lock();
        try {
            getCollectionMetaData(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName());
            return false;
        } catch (FileNotFoundException e) {
            fileStorageUtil.storeFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), "collectionMetaData", objectMapper.writeValueAsString(collectionMetaData));
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void addCollectionMetaDataDocument(CollectionMetaData collectionMetaData) throws JsonProcessingException {
        collectionMetaData.setNumOfDocuments(collectionMetaData.getNumOfDocuments() + 1);
        collectionMetaData.setLastId(collectionMetaData.getLastId() + 1);
        saveCollectionMetaData(collectionMetaData);
    }

    public void deleteCollectionMetaDataDocument(CollectionMetaData collectionMetaData) throws JsonProcessingException {
        collectionMetaData.setNumOfDocuments(collectionMetaData.getNumOfDocuments() - 1);
        saveCollectionMetaData(collectionMetaData);
    }

    public void saveCollectionSchema(CollectionMetaData collectionMetaData, String schema) {
        rwLock.writeLock().lock();
        try {
            fileStorageUtil.storeFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), "schema", schema);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void validateDocumentToSchema(CollectionMetaData collectionMetaData, JSONObject document) throws ValidationException, FileNotFoundException {
        rwLock.readLock().lock();
        try {
            Optional<JSONObject> optionalJSONObject = fileStorageUtil.getFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), "schema");
            if (optionalJSONObject.isPresent()) {
                Schema schema = SchemaLoader.load(optionalJSONObject.get());
                schema.validate(document);
            } else {
                throw new FileNotFoundException();
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }
}

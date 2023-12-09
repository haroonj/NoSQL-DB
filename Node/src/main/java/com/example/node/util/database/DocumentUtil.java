package com.example.node.util.database;

import com.example.node.exceptions.OptimisticLockingFailureException;
import com.example.node.model.metaData.CollectionMetaData;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class DocumentUtil {
    private final FileStorageUtil fileStorageUtil;
    private final ReentrantReadWriteLock rwLock;


    public DocumentUtil(FileStorageUtil fileStorageUtil) {
        this.fileStorageUtil = fileStorageUtil;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public Path saveDocument(CollectionMetaData collectionMetaData, JSONObject document) {
        rwLock.writeLock().lock();
        try {
            return fileStorageUtil.storeFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), document.get("_id").toString(), document.toString());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void saveIndexDocument(CollectionMetaData collectionMetaData, String fileName, String content) {
        rwLock.writeLock().lock();
        try {
            fileStorageUtil.storeFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), fileName, content);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Path updateDocument(CollectionMetaData collectionMetaData, JSONObject document) {
        rwLock.writeLock().lock();
        try {
            return fileStorageUtil.storeFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), document.get("_id").toString(), document.toString());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void deleteDocument(CollectionMetaData collectionMetaData, String fileName) {
        rwLock.writeLock().lock();
        try {
            fileStorageUtil.deleteFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), fileName);
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    public JSONObject getDocumentByFileName(String databaseName, String collectionName, String fileName) throws NoSuchElementException {
        rwLock.readLock().lock();
        try {
            Optional<JSONObject> optional = fileStorageUtil.getFile(databaseName, collectionName, fileName);
            if (optional.isPresent()) {
                return optional.get();
            } else {
                throw new NoSuchElementException();
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public JSONObject getDocumentByAbsolutePath(String absolutePath) {
        rwLock.readLock().lock();
        try {
            Optional<JSONObject> optional = fileStorageUtil.getFileByAbsolutePath(absolutePath);
            if (optional.isPresent()) {
                return optional.get();
            } else {
                throw new NoSuchElementException();
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<JSONObject> getDocumentByProperty(CollectionMetaData collectionMetaData, String property, String value) {
        rwLock.readLock().lock();
        try {
            List<JSONObject> documents = fileStorageUtil.getAllDocumentsInCollection(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName());
            List<JSONObject> filteredDocuments = new ArrayList<>();
            for (JSONObject jsonObject : documents) {
                if (jsonObject.get(property).toString().equals(value)) {
                    filteredDocuments.add(jsonObject);
                }
            }
            return filteredDocuments;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<JSONObject> getDocumentByPropertyContains(CollectionMetaData collectionMetaData, String property, String value) {
        rwLock.readLock().lock();
        try {
            List<JSONObject> documents = fileStorageUtil.getAllDocumentsInCollection(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName());
            List<JSONObject> filteredDocuments = new ArrayList<>();
            for (JSONObject jsonObject : documents) {
                if (jsonObject.get(property).toString().contains(value)) {
                    filteredDocuments.add(jsonObject);
                }
            }
            return filteredDocuments;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<JSONObject> getAllDocuments(String databaseName, String collectionName) {
        rwLock.readLock().lock();
        try {
            return fileStorageUtil.getAllDocumentsInCollection(databaseName, collectionName);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Path updateDocument(CollectionMetaData collectionMetaData, Map<String, Object> body) throws OptimisticLockingFailureException {
        rwLock.writeLock().lock();
        try {
            JSONObject document = getDocumentByFileName(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), body.get("id").toString());
            if (!body.get("oldValue").toString().equals(document.get(body.get("property").toString()).toString())) {
                throw new OptimisticLockingFailureException("Version conflict!");
            }
            document.remove(body.get("property").toString());
            document.put(body.get("property").toString(), body.get("newValue").toString());
            return updateDocument(collectionMetaData, document);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}


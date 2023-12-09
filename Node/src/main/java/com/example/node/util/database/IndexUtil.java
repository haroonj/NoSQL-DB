package com.example.node.util.database;

import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.metaData.IndexMetaData;
import com.example.node.model.system.Affinity;
import com.example.node.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class IndexUtil {
    private final CollectionUtil collectionUtil;
    private final DocumentUtil documentUtil;
    private final ReentrantReadWriteLock rwLock;


    public IndexUtil(CollectionUtil collectionUtil, DocumentUtil documentUtil) {
        this.collectionUtil = collectionUtil;
        this.documentUtil = documentUtil;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public void createIdIndex(String databaseName, String collectionName) {
        rwLock.writeLock().lock();
        try {
            HashMap<String, IndexMetaData> indexItems = new HashMap<>();
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(databaseName, collectionName);
            collectionMetaData.addIndexedProperty("_id");
            collectionUtil.saveCollectionMetaData(collectionMetaData);
            saveIndexFile(databaseName, collectionName, "_id", new JSONObject(indexItems).toString());

        } catch (JsonProcessingException | FileNotFoundException exception) {
            log.error(exception.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void indexNewDocument(String databaseName, String collectionName, JSONObject body, Path documentPath) {
        rwLock.writeLock().lock();
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(databaseName, collectionName);
            for (String propertyToIndex : collectionMetaData.getIndexedProperties()) {
                HashMap<String, IndexMetaData> indexItems = getIndexData(databaseName, collectionName, propertyToIndex + "Index");
                if (indexItems.containsKey(body.get(propertyToIndex).toString())) {
                    indexItems.get(body.getString(propertyToIndex)).addItem(documentPath.toString());
                } else {
                    indexItems.put(body.getString(propertyToIndex), new IndexMetaData(documentPath.toString()));
                }
                saveIndexFile(databaseName, collectionName, propertyToIndex, new JSONObject(indexItems).toString());
            }
        } catch (JsonProcessingException | FileNotFoundException exception) {
            log.error(exception.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean createPropertyIndex(String databaseName, String collectionName, String indexedProperty) throws FileNotFoundException, JsonProcessingException {
        rwLock.writeLock().lock();
        try {
            HashMap<String, IndexMetaData> indexItems;
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(databaseName, collectionName);
            if (!collectionMetaData.getIndexedProperties().contains(indexedProperty)) {
                collectionMetaData.addIndexedProperty(indexedProperty);
                collectionUtil.saveCollectionMetaData(collectionMetaData);
                indexItems = reflectIndex(databaseName, collectionName, indexedProperty);
                saveIndexFile(databaseName, collectionName, indexedProperty, new JSONObject(indexItems).toString());
                return true;
            } else {
                return false;
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public HashMap<String, IndexMetaData> getIndexData(String databaseName, String collectionName, String fileName) throws JsonProcessingException {
        rwLock.readLock().lock();
        try {
            JSONObject jsonObject = documentUtil.getDocumentByFileName(databaseName, collectionName, fileName);
            return JSONUtil.parseObject(jsonObject, new TypeReference<HashMap<String, IndexMetaData>>() {
            });
        } catch (NoSuchElementException noSuchElementException) {
            return new HashMap<>();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void saveIndexFile(String databaseName, String collectionName, String property, String document) {
        rwLock.writeLock().lock();
        try {
            documentUtil.saveIndexDocument(CollectionMetaData.builder()
                    .databaseName(databaseName)
                    .collectionName(collectionName)
                    .build(), property + "Index", document);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void deleteIndexFile(String databaseName, String collectionName, String property) {
        rwLock.writeLock().lock();
        try {
            documentUtil.deleteDocument(CollectionMetaData.builder()
                    .databaseName(databaseName)
                    .collectionName(collectionName)
                    .build(), property + "Index");
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public HashMap<String, IndexMetaData> reflectIndex(String databaseName, String collectionName, String indexedProperty) throws JsonProcessingException {
        rwLock.writeLock().lock();
        try {
            HashMap<String, IndexMetaData> idIndexItems = getIndexData(databaseName, collectionName, "_idIndex");
            HashMap<String, IndexMetaData> propertyIndexItems = new HashMap<>();
            for (IndexMetaData indexMetaData : idIndexItems.values()) {
                for (String path : indexMetaData.getItems()) {
                    JSONObject document = documentUtil.getDocumentByAbsolutePath(path);
                    String property = document.get(indexedProperty).toString();
                    if (propertyIndexItems.containsKey(property)) {
                        propertyIndexItems.get(property).addItem(path);
                    } else {
                        propertyIndexItems.put(property, new IndexMetaData(path));
                    }
                }
            }
            return propertyIndexItems;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void deleteIndex(String databaseName, String collectionName, String removedIndex) throws FileNotFoundException {
        rwLock.writeLock().lock();
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData(databaseName, collectionName);
            if (collectionMetaData.getIndexedProperties().contains(removedIndex)) {
                collectionMetaData.removeIndexedProperty(removedIndex);
                collectionUtil.saveCollectionMetaData(collectionMetaData);
                deleteIndexFile(databaseName, collectionName, removedIndex);
            } else {
                throw new FileNotFoundException();
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error(jsonProcessingException.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void deleteIndexedDocument(CollectionMetaData collectionMetaData, JSONObject document) throws JsonProcessingException {
        rwLock.writeLock().lock();
        try {
            for (String propertyIndex : collectionMetaData.getIndexedProperties()) {
                HashMap<String, IndexMetaData> indexItems = getIndexData(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), propertyIndex + "Index");
                IndexMetaData indexMetaData = indexItems.get(document.get(propertyIndex).toString());
                indexMetaData.removeItemById(document.get("_id").toString());
                if (indexMetaData.getItems().isEmpty()) {
                    indexItems.remove(document.get(propertyIndex).toString());
                } else {
                    indexItems.replace(document.get(propertyIndex).toString(), indexMetaData);
                }
                saveIndexFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), propertyIndex, new JSONObject(indexItems).toString());
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<JSONObject> getDocumentByProperty(CollectionMetaData collectionMetaData, String property, String value) throws JsonProcessingException {
        rwLock.readLock().lock();
        try {
            HashMap<String, IndexMetaData> indexItems = getIndexData(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), property + "Index");
            IndexMetaData indexMetaData = indexItems.get(value);
            List<JSONObject> documents = new ArrayList<>();
            for (String path : indexMetaData.getItems()) {
                documents.add(documentUtil.getDocumentByAbsolutePath(path));
            }
            return documents;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public JSONObject getDocumentByUniqueProperty(CollectionMetaData collectionMetaData, String property, String value) {
        rwLock.readLock().lock();
        try {
            HashMap<String, IndexMetaData> indexItems = getIndexData(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), property + "Index");
            IndexMetaData indexMetaData = indexItems.get(value);
            return documentUtil.getDocumentByAbsolutePath(indexMetaData.getItems().get(0));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void updateDocument(CollectionMetaData collectionMetaData, Map<String, Object> body, Path path) throws JsonProcessingException {
        rwLock.writeLock().lock();
        try {
            for (String property : collectionMetaData.getIndexedProperties()) {
                if (!property.equals("_id") && property.equals(body.get("property").toString())) {
                    HashMap<String, IndexMetaData> indexItems = getIndexData(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), property + "Index");
                    indexItems.get(body.get("oldValue").toString()).removeItemById(body.get("id").toString());
                    if (indexItems.containsKey(body.get("newValue").toString())) {
                        indexItems.get(body.get("newValue").toString()).addItem(path.toString());
                    } else {
                        indexItems.put(body.get("newValue").toString(), new IndexMetaData(path.toString()));
                    }
                    saveIndexFile(collectionMetaData.getDatabaseName(), collectionMetaData.getCollectionName(), body.get("property").toString(), new JSONObject(indexItems).toString());
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<Affinity> deleteAffinitiesByContaining(String subName) {
        rwLock.writeLock().lock();
        try {
            CollectionMetaData collectionMetaData = collectionUtil.getCollectionMetaData("System", "Affinity");
            List<JSONObject> jsonObjects = documentUtil.getDocumentByPropertyContains(collectionMetaData, "_documentId", subName);
            for (JSONObject affinity : jsonObjects) {
                documentUtil.deleteDocument(collectionMetaData, affinity.get("_id").toString());
                deleteIndexedDocument(collectionMetaData, affinity);
                collectionUtil.deleteCollectionMetaDataDocument(collectionMetaData);
            }
            return JSONUtil.parseJsonListToList(jsonObjects, Affinity.class);
        } catch (FileNotFoundException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}

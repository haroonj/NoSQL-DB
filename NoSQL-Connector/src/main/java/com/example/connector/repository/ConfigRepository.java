package com.example.connector.repository;

import org.json.JSONObject;

interface ConfigRepository {
    boolean createDatabase();

    boolean createCollection(String collectionName, JSONObject schema);

    boolean deleteDatabase();

    boolean deleteCollection(String collectionName);
}

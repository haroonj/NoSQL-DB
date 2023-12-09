package com.example.node.util.database;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class FileStorageUtil {

    private final String ROOT_DIR = "storage";
    private final String FILE_EXTENSION = ".json";

    public boolean createDataBase(String databaseName) {
        Path folderPath = Paths.get(ROOT_DIR, databaseName);
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                log.error("Error while creating the database");
                throw new RuntimeException("Could not create directory: " + folderPath, e);
            }
            return true;
        }
        return false;
    }

    public Path createCollection(String databaseName, String collectionName) {
        Path folderPath = Paths.get(ROOT_DIR + "/" + databaseName, collectionName);
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                log.error("Error while creating the collection");
                throw new RuntimeException("Could not create directory: " + folderPath, e);
            }
        }
        return folderPath;
    }

    public Path storeFile(String databaseName, String collectionName, String fileName, String content) {
        Path folderPath = createCollection(databaseName, collectionName);
        Path filePath = folderPath.resolve(fileName + FILE_EXTENSION);
        try {
            Files.write(filePath, content.getBytes());
            return filePath;
        } catch (IOException e) {
            log.error("Error while storing the file");
            throw new RuntimeException("Could not store file: " + fileName + FILE_EXTENSION, e);
        }
    }

    public Optional<JSONObject> getFile(String databaseName, String collectionName, String fileName) {
        Path filePath = Paths.get(ROOT_DIR + "/" + databaseName, collectionName, fileName + FILE_EXTENSION);
        return getJsonObject(filePath);
    }

    public Optional<JSONObject> getFileByAbsolutePath(String absoluteFilePath) {
        Path filePath = Paths.get(absoluteFilePath);
        return getJsonObject(filePath);
    }

    private Optional<JSONObject> getJsonObject(Path filePath) {
        try {
            String content = new String(Files.readAllBytes(filePath));
            JSONObject jsonObject = new JSONObject(content);
            return Optional.of(jsonObject);
        } catch (IOException | JSONException e) {
            return Optional.empty();
        }
    }

    public void deleteDatabase(String databaseName) {
        try {
            Path folderPath = Paths.get(ROOT_DIR, databaseName);
            FileSystemUtils.deleteRecursively(folderPath);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting database: " + databaseName, e);
        }
    }

    public void deleteCollection(String databaseName, String collectionName) {
        try {
            Path folderPath = Paths.get(ROOT_DIR, databaseName, collectionName);
            FileSystemUtils.deleteRecursively(folderPath);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting database: " + databaseName, e);
        }
    }

    public void deleteFile(String databaseName, String collectionName, String fileName) {
        Path filePath = Paths.get(ROOT_DIR + "/" + databaseName, collectionName, fileName + FILE_EXTENSION);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error while deleting the file");
            throw new RuntimeException("Could not delete file: " + fileName + FILE_EXTENSION, e);
        }
    }

    public List<JSONObject> getAllDocumentsInCollection(String databaseName, String collectionName) {
        Path folderPath = Paths.get(ROOT_DIR + "/" + databaseName, collectionName);
        List<JSONObject> documents = new ArrayList<>();
        for (String fileName : Objects.requireNonNull(folderPath.toFile().list())) {
            if (fileName.contains(collectionName)) {
                Optional<JSONObject> optional = getFile(databaseName, collectionName, fileName.split(".json")[0]);
                optional.ifPresent(documents::add);
            }
        }
        return documents;
    }

}

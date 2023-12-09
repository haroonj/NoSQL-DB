package com.example.node.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JSONUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> String generateJsonSchema(Class<T> classToInspect) {
        try {
            mapper.setVisibility(mapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

            JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
            JsonSchema schema = schemaGen.generateSchema(classToInspect);

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(JSONObject content, Class<T> valueType) {
        try {
            return mapper.readValue(content.toString(), valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(JSONObject content, TypeReference<T> valueType) {
        try {
            return mapper.readValue(content.toString(), valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(byte[] body, Class<T> valueType) {
        try {
            return mapper.readValue(body, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseJsonToList(String jsonString, Class<T> clazz) {
        JSONObject response = new JSONObject(jsonString);
        List<T> objects = new ArrayList<>();

        for (String key : response.keySet()) {
            JSONObject objJson = response.getJSONObject(key);
            T obj = mapJsonToObject(objJson, clazz);
            objects.add(obj);
        }

        return objects;
    }

    public static <T> List<T> parseJsonListToList(List<JSONObject> jsonObjectList, Class<T> clazz) {
        List<T> objects = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjectList) {
            objects.add(parseObject(jsonObject, clazz));
        }
        return objects;
    }

    private static <T> T mapJsonToObject(JSONObject jsonObject, Class<T> clazz) {
        T instance;
        try {
            instance = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (jsonObject.has(fieldName)) {
                    Object value = jsonObject.get(fieldName);
                    field.set(instance, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }
}
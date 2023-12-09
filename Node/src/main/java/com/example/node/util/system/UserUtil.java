package com.example.node.util.system;

import com.example.node.model.metaData.CollectionMetaData;
import com.example.node.model.system.User;
import com.example.node.util.JSONUtil;
import com.example.node.util.database.DocumentUtil;
import com.example.node.util.database.IndexUtil;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final IndexUtil indexUtil;
    private final DocumentUtil documentUtil;

    public UserUtil(IndexUtil indexUtil, DocumentUtil documentUtil) {
        this.indexUtil = indexUtil;
        this.documentUtil = documentUtil;
    }

    public User findByUsername(String username) {
        return JSONUtil.parseObject(
                indexUtil.getDocumentByUniqueProperty(CollectionMetaData.builder()
                                .databaseName("System")
                                .collectionName("User")
                                .build()
                        , "username", username), User.class);
    }

    public User findById(String id) {
        return JSONUtil.parseObject(
                documentUtil.getDocumentByFileName(
                        "System"
                        , "User"
                        , id
                ), User.class);
    }
}

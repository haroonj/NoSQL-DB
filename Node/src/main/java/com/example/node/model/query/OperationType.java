package com.example.node.model.query;

import lombok.Getter;

@Getter
public enum OperationType {
    CREATE_DATABASE(Affinity.FALSE, Category.CREATE),
    CREATE_COLLECTION(Affinity.FALSE, Category.CREATE),
    CREATE_INDEX(Affinity.FALSE, Category.CREATE),
    CREATE_DOCUMENT(Affinity.FALSE, Category.CREATE),

    READ_BY_ID(Affinity.FALSE, Category.READ),
    READ_ALL(Affinity.FALSE, Category.READ),
    READ_DOCUMENT_BY_PROPERTY(Affinity.FALSE, Category.READ),

    UPDATE(Affinity.TRUE, Category.UPDATE),

    DELETE_DATABASE(Affinity.FALSE, Category.DELETE),
    DELETE_COLLECTION(Affinity.FALSE, Category.DELETE),
    DELETE_INDEX(Affinity.FALSE, Category.DELETE),
    DELETE_DOCUMENT(Affinity.TRUE, Category.DELETE),

    INVALID(Affinity.INVALID, Category.INVALID);

    private final Affinity affinity;
    private final Category category;

    OperationType(Affinity affinity, Category category) {
        this.affinity = affinity;
        this.category = category;
    }

    public enum Affinity {
        TRUE,
        FALSE,
        INVALID
    }

    public enum Category {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        INVALID
    }

    public static boolean hasAffinity(OperationType operationType) {
        try {
            return (operationType.getAffinity().equals(Affinity.TRUE));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isBroadCastable(OperationType operationType) {
        try {
            return !operationType.getCategory().equals(Category.READ);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}


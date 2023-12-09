package com.example.node.model.system;

import lombok.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Affinity {
    private String _id;
    private String _nodeId;
    private String _documentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Affinity affinity = (Affinity) o;
        return Objects.equals(_id, affinity._id) && Objects.equals(_nodeId, affinity._nodeId) && Objects.equals(_documentId, affinity._documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _nodeId, _documentId);
    }
}

package com.example.bootstrapping.model.query;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateDocumentQuery {
    private String id;
    private String property;
    private String oldValue;
    private String newValue;
}

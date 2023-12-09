package com.example.connector.model.connection;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NoSQLConfig {
    private String bootStrappingNodeUrl;
    private String nodeNodeUrl;
    private String database;
    private String user;
    private String password;
    private String token;
}

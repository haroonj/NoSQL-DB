package com.example.connector.connection;

import com.example.connector.model.connection.NoSQLConfig;
import com.example.connector.model.request.JwtRequest;
import com.example.connector.model.request.QueryRequest;
import com.example.connector.model.request.User;
import com.example.connector.model.response.LoginResponse;
import com.example.connector.model.response.QueryResponse;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Data
@ToString
public class NoSQLDatabaseConnection {

    private final NoSQLConfig config = new NoSQLConfig();
    private final NoSQLCommunicationProtocol noSQLCommunicationProtocol = new NoSQLCommunicationProtocol();
    private static volatile NoSQLDatabaseConnection instance;
    private static final Object lock = new Object();

    public static NoSQLDatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new NoSQLDatabaseConnection();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        instance.logout();
                    }));
                }
            }
        }
        return instance;
    }

    private NoSQLDatabaseConnection() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (input == null) {
                throw new IOException("Unable to find db.properties");
            }
            properties.load(input);
            this.config.setBootStrappingNodeUrl(getPropertyValue(properties, "NoSQL.Connection.url"));
            this.config.setDatabase(getPropertyValue(properties, "NoSQL.Connection.database"));
            this.config.setUser(getPropertyValue(properties, "NoSQL.Connection.username"));
            this.config.setPassword(getPropertyValue(properties, "NoSQL.Connection.password"));
            if (getPropertyValue(properties, "NoSQL.Connection.create").equalsIgnoreCase("true"))
                register();
            else
                login();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }



    public QueryResponse post(QueryRequest queryRequest) {
        return noSQLCommunicationProtocol.post(this.config.getNodeNodeUrl(), this.config.getToken()
                , new JSONObject(queryRequest), "/query/processQuery");
    }

    private void login() {
        JwtRequest user = new JwtRequest(this.config.getUser(), this.config.getPassword());
        LoginResponse loginResponse = noSQLCommunicationProtocol.login(this.config.getBootStrappingNodeUrl()
                , new JSONObject(user),"user/login");
        this.config.setNodeNodeUrl(loginResponse.getNodeUrl());
        this.config.setToken(loginResponse.getJwtToken());
    }
    private void register() {
        User user = User.builder()
                .username(this.config.getUser())
                .password(this.config.getPassword())
                .full_name(this.config.getUser())
                .build();
        LoginResponse loginResponse = noSQLCommunicationProtocol.login(this.config.getBootStrappingNodeUrl()
                , new JSONObject(user),"user/register");
        this.config.setNodeNodeUrl(loginResponse.getNodeUrl());
        this.config.setToken(loginResponse.getJwtToken());
    }
    private void logout() {
        noSQLCommunicationProtocol.logout(this.config.getBootStrappingNodeUrl(), this.config.getNodeNodeUrl());
        this.config.setToken("");
        System.out.println("logged out");
    }

    public String getPropertyValue(Properties properties, String propertyKey) {
        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue != null && propertyValue.startsWith("${") && propertyValue.contains(":")) {
            String envVarName = propertyValue.substring(2, propertyValue.indexOf(':'));
            String defaultVal = propertyValue.substring(propertyValue.indexOf(':') + 1, propertyValue.length() - 1);
            String envVarValue = System.getenv(envVarName);
            return (envVarValue != null && !envVarValue.isEmpty()) ? envVarValue : defaultVal;
        }
        return propertyValue;
    }
}

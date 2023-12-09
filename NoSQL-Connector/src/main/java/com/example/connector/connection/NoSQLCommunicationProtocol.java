package com.example.connector.connection;

import com.example.connector.model.response.LoginResponse;
import com.example.connector.model.response.QueryResponse;
import com.example.connector.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class NoSQLCommunicationProtocol {

    public QueryResponse post(String url, String token, JSONObject request, String path) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder uriBuilder = new URIBuilder(url).setPath(path);
            HttpPost httpPost = new HttpPost(uriBuilder.build());

            String jsonBody = request.toString();
            StringEntity entity = new StringEntity(jsonBody);
            httpPost.setEntity(entity);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + token);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code: " + statusCode);
            return JSONUtil.parseObject(EntityUtils.toString(response.getEntity()), QueryResponse.class);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public LoginResponse login(String url, JSONObject request,String path) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            System.out.println(url);
            URIBuilder uriBuilder = new URIBuilder(url).setPath(path);
            HttpPost httpPost = new HttpPost(uriBuilder.build());

            String jsonBody = request.toString();
            StringEntity entity = new StringEntity(jsonBody);
            httpPost.setEntity(entity);

            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code: " + statusCode);
            return JSONUtil.parseObject(EntityUtils.toString(response.getEntity()), LoginResponse.class);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
    public void logout(String url, String nodeUrl) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder uriBuilder = new URIBuilder(url).setPath("user/logout");
            HttpPost httpPost = new HttpPost(uriBuilder.build());

            StringEntity entity = new StringEntity(nodeUrl);
            httpPost.setEntity(entity);

            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code: " + statusCode);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}

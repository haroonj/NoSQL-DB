package com.example.bootstrapping.service;


import com.example.bootstrapping.model.request.JwtRequest;
import com.example.bootstrapping.model.request.QueryRequest;
import com.example.bootstrapping.model.response.JwtResponse;
import com.example.bootstrapping.model.response.QueryResponse;
import com.example.bootstrapping.model.system.Node;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
public class CommunicationService {
    private final WebClient webClient;


    public CommunicationService() {
        this.webClient = WebClient.builder().build();

    }

    public Flux<QueryResponse> sendPostRequestsToMultipleUrls(List<Node> nodeList, QueryRequest body, String path, String token) {
        return Flux.fromIterable(nodeList)
                .flatMap(node -> sendPostRequest(node, body, path, token))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public void sendPostRequestsToMultipleUrls(List<Node> nodeList, QueryRequest body, String path) {
        for (Node node : nodeList) {
            log.info("sendPostRequest to " + node);
            log.info(body.toString());
            webClient.post()
                    .uri(node.getUrl(), uriBuilder -> uriBuilder
                            .path(path)
                            .build())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(QueryResponse.class).block();
        }
    }

    public Mono<QueryResponse> sendPostRequest(Node node, QueryRequest body, String path, String token) {
        log.info("sendPostRequest");
        log.info(body.toString());
        return webClient.post()
                .uri(node.getUrl(), uriBuilder -> uriBuilder
                        .path(path)
                        .build())
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(QueryResponse.class);
    }

    public JwtResponse login(Node node, JwtRequest body) {
        log.info("sendPostRequest");
        log.info(node.toString());
        log.info(body.toString());
        return webClient.post()
                .uri(node.getUrl(), uriBuilder -> uriBuilder
                        .path("/user/authenticate")
                        .build())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JwtResponse.class).block();
    }
}

package com.example.node.services;

import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.QueryResponse;
import com.example.node.model.system.Node;
import com.example.node.util.system.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JWTUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private String token;

    public CommunicationService(JWTUtil jwtUtil, CustomUserDetailService customUserDetailService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailService = customUserDetailService;
        this.webClient = WebClient.builder().build();
        this.token = "";
    }

    public Flux<QueryResponse> sendPostRequestsToMultipleUrls(List<Node> nodeList, QueryRequest body, String path) {
        return Flux.fromIterable(nodeList)
                .flatMap(node -> sendPostRequest(node.getUrl(), body, path))
                .subscribeOn(Schedulers.boundedElastic());
    }


    public Mono<QueryResponse> sendPostRequest(String baseUrl, QueryRequest body, String path) {
        log.info("sendPostRequest");
        return webClient.post()
                .uri(baseUrl, uriBuilder -> uriBuilder.
                        path("broadCast/")
                        .path(path)
                        .build())
                .header("Authorization", "Bearer " + getToken())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(QueryResponse.class);
    }

    private String getToken() {
        if (this.token.isEmpty()) {
            UserDetails userDetails = customUserDetailService.loadUserByUsername("admin");
            this.token = jwtUtil.generateToken(userDetails);
        }
        return this.token;
    }
}

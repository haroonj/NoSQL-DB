package com.example.bootstrapping.service;

import com.example.bootstrapping.model.request.CreateContainerRequest;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DockerService {

    private final DockerClient dockerClient;

    public DockerService() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    public String createAndStartContainer(CreateContainerRequest createContainerRequest) throws RuntimeException {
        System.out.println(createContainerRequest);
        ExposedPort exposedPort = ExposedPort.tcp(createContainerRequest.getPort());
        Ports portBindings = new Ports();
        portBindings.bind(exposedPort, Ports.Binding.bindPort(createContainerRequest.getPort()));

        List<String> environmentVariables = Arrays.asList("node_port=" + createContainerRequest.getPort(), "node_url=" + createContainerRequest.getContainerUrl());

        CreateContainerResponse container = dockerClient.createContainerCmd(createContainerRequest.getImageName())
                .withName(createContainerRequest.getContainerName())
                .withExposedPorts(exposedPort)
                .withPortBindings(portBindings)
                .withEnv(environmentVariables)
                .exec();

        dockerClient.connectToNetworkCmd()
                .withContainerId(container.getId())
                .withNetworkId(createContainerRequest.getNetworkName())
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
        return container.getId();
    }
}

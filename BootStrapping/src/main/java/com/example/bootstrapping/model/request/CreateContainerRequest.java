package com.example.bootstrapping.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateContainerRequest {
    private String imageName;
    private String networkName;
    private String containerName;
    private String containerUrl;
    private int port;
}

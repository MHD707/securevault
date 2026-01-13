package com.example.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {
    @Bean
    @LoadBalanced // permet à WebClient de résoudre les noms via Eureka
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}


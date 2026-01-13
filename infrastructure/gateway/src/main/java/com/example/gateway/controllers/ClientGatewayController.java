package com.example.gateway.controllers;

import com.example.gateway.entity.ClientModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/gateway/clients")
public class ClientGatewayController {

    private final WebClient.Builder webClientBuilder;

    public ClientGatewayController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    // ======================
// GET all clients
// ======================
    @GetMapping
    public Mono<List<ClientModel>> getAllClients() { //OBJET MONO POUR LES LISTES
        return webClientBuilder.build()
                .get()
                .uri("http://AUTH-SERVICE/api/users")
                .retrieve()
                .bodyToFlux(ClientModel.class)
                .collectList();
    }

    @GetMapping("/{id}")
    public Mono<ClientModel> getClientById(@PathVariable Long id) {
        return webClientBuilder.build()
                .get()
                .uri("http://AUTH-SERVICE/api/users/{id}", id) // Adapt the URI to your microservice path!
                .retrieve()
                .bodyToMono(ClientModel.class);
    }


    @PostMapping
    public Mono<ClientModel> createClient(@RequestBody ClientModel client) {
        return webClientBuilder.build()
                .post()
                .uri("http://AUTH-SERVICE/api/users")
                .bodyValue(client)
                .retrieve()
                .bodyToMono(ClientModel.class);

    }

    // ======================
// PUT update client
// ======================
    @PutMapping("/{id}")
    public Mono<ClientModel> updateClient(@PathVariable Long id, @RequestBody
    ClientModel client) {
        return webClientBuilder.build()
                .put()
                .uri("http://AUTH-SERVICE/api/users/{id}", id)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(ClientModel.class);
    }
    // ======================
// DELETE client
// ======================
    @DeleteMapping("/{id}")
    public Mono<Void> deleteClient(@PathVariable Long id) {
        return webClientBuilder.build()
                .delete()
                .uri("http://AUTH-SERVICE/api/users/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
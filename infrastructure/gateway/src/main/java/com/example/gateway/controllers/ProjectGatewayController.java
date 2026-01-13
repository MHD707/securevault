package com.example.gateway.controllers;

import com.example.gateway.dtos.ProjectDetailsModel;
import com.example.gateway.entity.ClientModel;
import com.example.gateway.entity.ProjectModel;
import com.example.gateway.entity.SourceModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gateway/projects")
public class ProjectGatewayController {

    private final WebClient.Builder webClientBuilder;

    public ProjectGatewayController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // ======================
    // GET all projects
    // ======================
    @GetMapping
    public Mono<List<ProjectModel>> getAllProjects() {
        return webClientBuilder.build()
                .get()
                .uri("http://PROJECT-SERVICE/api/v1/projects")
                .retrieve()
                .bodyToFlux(ProjectModel.class)
                .collectList();
    }

    // ======================
    // GET single project by ID
    // ======================
//    @GetMapping("/{id}")
//    public Mono<ProjectModel> getProject(@PathVariable UUID id) {
//        return webClientBuilder.build()
//                .get()
//                .uri("http://PROJECT-SERVICE/api/v1/projects/{id}", id)
//                .retrieve()
//                .bodyToMono(ProjectModel.class);
//    }
    @GetMapping("/{id}/details")
    public Mono<ProjectDetailsModel> getProjectDetails(@PathVariable UUID id) {
        WebClient client = webClientBuilder.build();

        Mono<ProjectModel> projectMono = client.get()
                .uri("http://PROJECT-SERVICE/api/v1/projects/{id}", id)
                .retrieve()
                .bodyToMono(ProjectModel.class);

        return projectMono.flatMap(project -> {
            // Add null check for userId
            if (project.getUserId() == null) {
                return Mono.error(new IllegalStateException(
                        "Project " + id + " has no associated user. userId is null."
                ));
            }

            return client.get()
                    .uri("http://AUTH-SERVICE/api/users/{userId}", project.getUserId())
                    .retrieve()
                    .bodyToMono(ClientModel.class)
                    .map(user -> new ProjectDetailsModel(project, user));
        });
    }

    // ======================
    // POST create project
    // ======================
    @PostMapping
    public Mono<ProjectModel> createProject(@RequestBody ProjectModel project) {
        return webClientBuilder.build()
                .post()
                .uri("http://PROJECT-SERVICE/api/v1/projects")
                .bodyValue(project)
                .retrieve()
                .bodyToMono(ProjectModel.class);
    }

    // ======================
    // PUT update project
    // ======================
    @PutMapping("/{id}")
    public Mono<ProjectModel> updateProject(@PathVariable UUID id, @RequestBody ProjectModel project) {
        return webClientBuilder.build()
                .put()
                .uri("http://PROJECT-SERVICE/api/v1/projects/{id}", id)
                .bodyValue(project)
                .retrieve()
                .bodyToMono(ProjectModel.class);
    }

    // ======================
    // DELETE project
    // ======================
    @DeleteMapping("/{id}")
    public Mono<Void> deleteProject(@PathVariable UUID id) {
        return webClientBuilder.build()
                .delete()
                .uri("http://PROJECT-SERVICE/api/v1/projects/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    // ======================
    // POST add source to project
    // ======================
    @PostMapping("/{id}/sources")
    public Mono<SourceModel> addSource(@PathVariable UUID id, @RequestBody SourceModel source) {
        return webClientBuilder.build()
                .post()
                .uri("http://PROJECT-SERVICE/api/v1/projects/{id}/sources", id)
                .bodyValue(source)
                .retrieve()
                .bodyToMono(SourceModel.class);
    }

    // ======================
    // GET all sources for a project
    // ======================
    @GetMapping("/{id}/sources")
    public Mono<List<SourceModel>> getProjectSources(@PathVariable UUID id) {
        return webClientBuilder.build()
                .get()
                .uri("http://PROJECT-SERVICE/api/v1/projects/{id}/sources", id)
                .retrieve()
                .bodyToFlux(SourceModel.class)
                .collectList();
    }
}

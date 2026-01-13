package com.example.gateway.dtos;

import com.example.gateway.entity.ClientModel;
import com.example.gateway.entity.ProjectModel;

public class ProjectDetailsModel {
    private ProjectModel project;
    private ClientModel user;

    public ProjectDetailsModel(ProjectModel project, ClientModel user){
        this.project = project;
        this.user = user;
    }
    public ProjectModel getProject() {
        return project;
    }

    public void setProject(ProjectModel project) {
        this.project = project;
    }

    public ClientModel getUser() {
        return user;
    }

    public void setUser(ClientModel user) {
        this.user = user;
    }

    //getters/seters
}

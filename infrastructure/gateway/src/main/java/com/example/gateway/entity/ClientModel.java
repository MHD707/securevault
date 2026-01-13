package com.example.gateway.entity;

import java.time.LocalDateTime;

public class ClientModel {

    private Long id;
    private String username;
    private String password;
    private String roles;

    // Constructors
    public ClientModel() {
    }

    public ClientModel(Long id, String username, String password, String roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "ClientModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
//    private Long id;
//    private String name;
//    private String email;
//    private String telephone;
//    private String adresse;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//    // Constructeurs
//    public ClientModel() {
//    }
//
//    public ClientModel(Long id, String name, String email, String telephone, String adresse) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.telephone = telephone;
//        this.adresse = adresse;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getTelephone() {
//        return telephone;
//    }
//
//    public void setTelephone(String telephone) {
//        this.telephone = telephone;
//    }
//
//    public String getAdresse() {
//        return adresse;
//    }
//
//    public void setAdresse(String adresse) {
//        this.adresse = adresse;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    // Méthodes utilitaires
//    @Override
//    public String toString() {
//        return "ClientModel{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", phone='" + telephone + '\'' +
//                ", address='" + adresse + '\'' +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
//                '}';
//    }
}
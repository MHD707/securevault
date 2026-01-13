package com.example.vaultservice.controllers;

import com.example.vaultservice.entities.Credential;
import com.example.vaultservice.services.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault/credentials")
public class CredentialController {

    @Autowired
    private CredentialService credentialService;

    @PostMapping
    public Credential createCredential(@RequestBody Credential credential, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        credential.setUserId(userId);
        return credentialService.saveCredential(credential);
    }

    @GetMapping
    public List<Credential> getAllCredentials(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return credentialService.getCredentialsByUserId(userId);
    }

    @GetMapping("/{id}")
    public Credential getCredential(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return credentialService.getCredentialById(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteCredential(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        credentialService.deleteCredential(id, userId);
    }
}

package com.example.vaultservice.services;

import com.example.vaultservice.entities.Credential;
import com.example.vaultservice.repositories.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private EncryptionService encryptionService;

    public Credential saveCredential(Credential credential) {
        credential.setEncryptedPassword(encryptionService.encrypt(credential.getEncryptedPassword()));
        return credentialRepository.save(credential);
    }

    public List<Credential> getCredentialsByUserId(Long userId) {
        return credentialRepository.findByUserId(userId).stream()
                .map(this::decryptCredential)
                .collect(Collectors.toList());
    }

    public Credential getCredentialById(Long id, Long userId) {
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

        if (!credential.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to credential");
        }

        return decryptCredential(credential);
    }

    public void deleteCredential(Long id, Long userId) {
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

        if (!credential.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to credential");
        }

        credentialRepository.delete(credential);
    }

    private Credential decryptCredential(Credential credential) {
        credential.setEncryptedPassword(encryptionService.decrypt(credential.getEncryptedPassword()));
        return credential;
    }
}

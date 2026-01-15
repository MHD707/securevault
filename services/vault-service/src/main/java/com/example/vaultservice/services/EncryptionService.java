package com.example.vaultservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${encryption.secret-key}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    @PostConstruct
    public void validateKeyLength() {
        int keyLength = secretKey.getBytes(StandardCharsets.UTF_8).length;
        if (keyLength != 16 && keyLength != 24 && keyLength != 32) {
            throw new IllegalStateException(
                "Invalid AES key length: " + keyLength + " bytes. " +
                "Configure encryption.secret-key as 16, 24, or 32 bytes."
            );
        }
    }

    public String encrypt(String value) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.getMessage());
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedValue);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.getMessage());
        }
    }
}

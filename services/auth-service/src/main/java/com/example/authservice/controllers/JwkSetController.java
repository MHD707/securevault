package com.example.authservice.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
public class JwkSetController {

    private final RSAPublicKey publicKey;
    private final String keyId;

    public JwkSetController(
            RSAPublicKey publicKey,
            @Value("${security.jwt.key-id:securevault-key-1}") String keyId
    ) {
        this.publicKey = publicKey;
        this.keyId = keyId;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        RSAKey key = new RSAKey.Builder(this.publicKey)
                .keyID(this.keyId)
                .build();
        return new JWKSet(key).toJSONObject();
    }
}

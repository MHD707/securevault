package com.example.authservice.services;

import com.example.authservice.dtos.*;
import com.example.authservice.entities.RefreshToken;
import com.example.authservice.entities.User;
import com.example.authservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private MfaService mfaService;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .mfaEnabled(false)
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.isMfaEnabled()) {
            return AuthResponse.builder()
                    .email(user.getEmail())
                    .mfaRequired(true)
                    .build();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenService.generateToken(authentication, user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .mfaRequired(false)
                .build();
    }

    public AuthResponse verifyMfa(MfaVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!mfaService.verifyCode(request.getCode(), user.getMfaSecret())) {
            throw new RuntimeException("Invalid MFA code");
        }

        String jwt = tokenService.generateToken(user.getEmail(), user.getRole(), user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .mfaRequired(false)
                .build();
    }

    public Map<String, String> setupMfa(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = mfaService.generateSecret();
        user.setMfaSecret(secret);
        userRepository.save(user);

        String qrCode = mfaService.getQrCode(secret, user.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("secret", secret);
        response.put("qrCode", qrCode);
        return response;
    }

    public void confirmMfaSetup(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (mfaService.verifyCode(code, user.getMfaSecret())) {
            user.setMfaEnabled(true);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid MFA code");
        }
    }

    public AuthResponse refreshToken(RefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = tokenService.generateToken(user.getEmail(), user.getRole(), user.getId());
                    return AuthResponse.builder()
                            .accessToken(token)
                            .refreshToken(request.getRefreshToken())
                            .email(user.getEmail())
                            .mfaRequired(false)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUserId(user.getId());
    }
}

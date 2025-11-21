package com.jrm.app.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, String> tokenToEmail = new ConcurrentHashMap<>();

    public String login(String email, String password) {
        // demo-only: accept any credentials and mint a token
        String token = UUID.randomUUID().toString();
        tokenToEmail.put(token, email);
        return token;
    }

    public void logout(String token) {
        tokenToEmail.remove(token);
    }

    public String getEmail(String token) {
        return tokenToEmail.get(token);
    }
}

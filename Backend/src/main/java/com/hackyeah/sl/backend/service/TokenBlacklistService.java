package com.hackyeah.sl.backend.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private final Cache<String, Boolean> tokenBlacklist;

    public TokenBlacklistService() {
        this.tokenBlacklist = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) // Tokens expire after 30 minutes
                .build();
    }

    public void blacklistToken(String token) {
        tokenBlacklist.put(token, true);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.getIfPresent(token) != null;
    }
}

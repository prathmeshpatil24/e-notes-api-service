package com.enotes.service;

public interface TokenBlockService {

    void blockToken(String token);

    void deleteExpiredToken();

    boolean isTokenBlocked(String token);
}

package com.enotes.service;

import org.springframework.cache.Cache;

public interface CacheService {

    Cache getCache(String cacheName);

    void getAllCache();
}

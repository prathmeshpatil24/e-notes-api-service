package com.enotes.service.impl;

import com.enotes.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheManager cacheManager;

    //get all caches present in memory
    public void getAllCache(){
        Collection<String> cacheNames = cacheManager.getCacheNames();


        cacheNames.stream()
                .forEach(System.out::println);

    }

    public Cache getCache(String cacheName){
        return cacheManager.getCache(cacheName);

    }


}

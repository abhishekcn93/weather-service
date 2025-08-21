package com.weather.service.impl;

import com.weather.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CacheServiceImpl implements CacheService {

    private final CacheManager simpleCacheManager;

    @Override
    public Collection<Object> getCacheDetailsByName(String name) {
        ConcurrentMapCache concurrentMapCache = (ConcurrentMapCache) simpleCacheManager.getCache(name);
        return Optional.ofNullable(concurrentMapCache).isPresent()? concurrentMapCache.getNativeCache().values() : new ArrayList<>();
    }
}

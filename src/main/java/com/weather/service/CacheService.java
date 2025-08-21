package com.weather.service;

import java.util.Collection;

public interface CacheService {

    Collection<Object> getCacheDetailsByName(String name);
}

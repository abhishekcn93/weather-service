package com.weather.controller;

import com.weather.model.Weather;
import com.weather.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/cache")
@RestController
public class CacheManagerController {

    private final CacheService cacheService;

    @GetMapping
    public Collection<Object> getCacheByName(String name)
    {
        return cacheService.getCacheDetailsByName(name);
    }
}

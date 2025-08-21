package com.weather.service.impl;

import com.weather.model.Weather;
import com.weather.model.WeatherId;
import com.weather.repository.WeatherRepository;
import com.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class WeatherServiceImpl implements WeatherService {

    final WeatherRepository weatherRepository;
    final CacheManager cacheManager;


    @Override
    public List<Weather> getAll() {
        List<Weather> weatherList;
        ConcurrentMapCache cache = (ConcurrentMapCache) cacheManager.getCache("weather");
        if(Optional.ofNullable(cache).isEmpty() || cache.getNativeCache().isEmpty())
        {
            weatherList = weatherRepository.findAll();
            cache = new ConcurrentMapCache("weather");
            for(Weather weather : weatherList)
            {
                cache.putIfAbsent(weather.getWeatherId(), weather);
            }
        }
        else
        {
            weatherList = cache.getNativeCache().values().stream().map(o -> (Weather)o).toList();
        }
        return weatherList;
    }

    @Cacheable(value = "weather", key="new com.weather.model.WeatherId(#country,#city)")
    @Override
    public Weather getByCity(String country, String city) {
        Optional<Weather> weatherOptional = weatherRepository.findById(new WeatherId(country, city));
        return weatherOptional.orElse(new Weather());
    }

    @CachePut(value = "weather", key = "#weather.weatherId")
    @Override
    public Weather save(Weather weather) throws RuntimeException {
        boolean exists = weatherRepository.existsById(weather.getWeatherId());
        if(exists){
            log.error("Weather details already exists for city :"+weather.getWeatherId().getCity());
            throw new RuntimeException("Weather already exists");
        }
        else {
             return weatherRepository.save(weather);
        }
    }

    @CachePut(value = "weather", key="new com.weather.model.WeatherId(#country,#city)")
    @Override
    public Weather update(String weatherDetails, String country, String city) {
        WeatherId weatherId = new WeatherId(country, city);
        boolean exists = weatherRepository.existsById(weatherId);
        if(exists){
            return weatherRepository.save(new Weather(weatherId, weatherDetails));
        }
        else {
            log.error("Update failed : Weather details for given city {} not found : ",city);
            throw new RuntimeException("Weather not found");
        }
    }

    @CacheEvict(value = "weather", key="new com.weather.model.WeatherId(#country,#city)")
    @Override
    public void delete(String country, String city) {
        WeatherId weatherId = new WeatherId(country, city);
        Optional<Weather> weatherOptional = weatherRepository.findById(weatherId);
        if(weatherOptional.isPresent())
        {
            weatherRepository.delete(weatherOptional.get());
        }
        else
        {
            log.error("Failed to delete : Weather details not found for given input");
        }
    }

    @CacheEvict(value = "weather", allEntries = true)
    @Override
    public void deleteAll() {
        weatherRepository.deleteAll();
    }


}

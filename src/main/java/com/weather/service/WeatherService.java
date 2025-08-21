package com.weather.service;

import com.weather.model.Weather;

import java.util.List;

public interface WeatherService {

    List<Weather> getAll();
    Weather getByCity(String country, String city);
    Weather save(Weather weather) throws RuntimeException;
    Weather update(String weatherDetails, String country, String city);
    void delete(String country, String city);
    void deleteAll();
}

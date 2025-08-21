package com.weather.controller;

import com.weather.model.Weather;
import com.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public List<Weather> getAll()
    {
        return weatherService.getAll();
    }

    @GetMapping("/{country}/{city}")
    public Weather getByCity(@PathVariable String country, @PathVariable String city)
    {
        return weatherService.getByCity(country, city);
    }

    @PostMapping
    public Weather save(@RequestBody Weather weather)
    {
        return weatherService.save(weather);
    }

    @PutMapping
    public Weather update(String weatherDetails, String country, String city)
    {
        return weatherService.update(weatherDetails, country, city);
    }

    @DeleteMapping("/{country}/{city}")
    public void delete(@PathVariable String country, @PathVariable String city)
    {
        weatherService.delete(country, city);
    }

    @DeleteMapping
    public void deleteAll()
    {
        weatherService.deleteAll();
    }
}


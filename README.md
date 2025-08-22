🚀 Caching in Spring Boot

❓ What is Caching?

Caching is the process of storing frequently accessed data in a temporary storage (cache) so that future requests can be served faster without always hitting the database or external service.

⚡ Why do we need Caching?

✅ Reduce database load

✅ Improve application performance

✅ Lower latency for repeated queries

✅ Save cost on expensive operations (API calls, DB joins, computations)

🏷️ Types of Caching

1) In-memory Cache → Data stored in application memory (fast, local).

2) Distributed Cache → Shared cache across multiple servers (e.g., Redis, Hazelcast).

3) Persistent Cache → Data survives application restarts (e.g., Redis, Ehcache).

🌱 Spring Boot Caching

Spring Boot provides out-of-the-box caching support (makes it easier to plug in providers like Ehcache, Caffeine, Redis, Hazelcast etc.) via spring-boot-starter-cache.
You just enable caching, choose a provider, and use annotations.

🔗 Supported Cache Providers

Spring Boot supports multiple cache backends:

1) Simple in-memory cache (default)

2) Caffeine

3) Ehcache

4) Hazelcast

5) Redis

6) JCache (JSR-107)

🧩 Simple In-Memory Cache (Default — currently used in this project)

1) Spring Boot automatically wires up a SimpleCacheManager backed by a ConcurrentHashMap.

2) This is the default caching implementation in Spring Boot.

3) It is fast and lightweight, but works only inside the running application (not shared across multiple instances).

4) Perfect for local development and small projects, but not ideal for production if you run multiple app instances.

✅ Setup
```
@EnableCaching //Enabling caching in application
@SpringBootApplication
public class WeatherServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherServiceApplication.class, args);
	}

}
```

✅ Common Annotations

1) @Cacheable → Stores method result in cache

2) @CachePut → Updates/refreshes the cache without skipping method execution

3) @CacheEvict → Removes entries from cache

4) @Caching → Combines multiple caching annotations

✅ Example (using in your project)

```
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class WeatherServiceImpl implements WeatherService 
{

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
```

Happy Coding 😎...........

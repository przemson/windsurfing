package pl.trzaskala.windsurfing.service;

import org.springframework.http.ResponseEntity;

public interface WeatherService<T> {
    /**
     * Returns forecast for a location
     * @param location place to get forecast for
     * @return forecast
     */
    ResponseEntity<String> getForecast(T location);
}

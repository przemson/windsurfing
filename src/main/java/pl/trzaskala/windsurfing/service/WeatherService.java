package pl.trzaskala.windsurfing.service;

import org.springframework.http.ResponseEntity;
import pl.trzaskala.windsurfing.model.Location;

public interface WeatherService {
    /**
     * Returns forecast for a location
     * @param location place to get forecast for
     * @return forecast
     */
    ResponseEntity<String> getForecast(Location location);
}

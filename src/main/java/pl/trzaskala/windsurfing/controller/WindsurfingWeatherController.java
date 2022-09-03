package pl.trzaskala.windsurfing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.trzaskala.windsurfing.model.Location;
import pl.trzaskala.windsurfing.service.JsonHelper;
import pl.trzaskala.windsurfing.service.WindsurfingWeatherService;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/weather")
public class WindsurfingWeatherController {
    private final WindsurfingWeatherService weatherService;
    @Value("classpath:locations.json")
    private Resource resource;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public WindsurfingWeatherController(
            WindsurfingWeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(value = "/windsurfing-locations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get best windsurfing location based on weather forecast for a given date",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    public ResponseEntity<Location> getBestWindsurfingLocation(
            @RequestParam(value = "date") @DateTimeFormat(pattern = DATE_FORMAT) Date date) throws IOException {
        List<Location> locations = JsonHelper.readLocations(resource.getInputStream());
        locations.forEach(location -> {
            String response = weatherService.getForecast(location).getBody();
            location.setForecast(JsonHelper.extractForecastForDate(date, DATE_FORMAT, response));
        });

        return new ResponseEntity<>(getBestWindsurfingConditions(locations), HttpStatus.OK);
    }

    private Location getBestWindsurfingConditions(List<Location> locationList) {
        Optional<Location> bestLocation =
                locationList.stream().filter(location -> location.getForecast().isSuitableForWindsurfing())
                            .max(Comparator.comparing(location -> location.getForecast().calculateWindsurfingScore()));
        return bestLocation.orElse(null);
    }

}

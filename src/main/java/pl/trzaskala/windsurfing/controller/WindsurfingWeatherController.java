package pl.trzaskala.windsurfing.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.trzaskala.windsurfing.model.Forecast;
import pl.trzaskala.windsurfing.model.Location;
import pl.trzaskala.windsurfing.service.JsonParserService;
import pl.trzaskala.windsurfing.service.WindsurfingWeatherService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/weather")
public class WindsurfingWeatherController {
    private final WindsurfingWeatherService weatherService;
    private final JsonParserService jsonParserService;

    @Value("classpath:locations.json")
    private Resource resource;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public WindsurfingWeatherController(
            WindsurfingWeatherService weatherService, JsonParserService jsonParserService) {
        this.weatherService = weatherService;
        this.jsonParserService = jsonParserService;
    }

    @GetMapping(value = "/windsurfing-locations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get best windsurfing location based on weather forecast for a given date",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    public ResponseEntity<Location> getBestWindsurfingLocation(
            @RequestParam(value = "date") @DateTimeFormat(pattern = DATE_FORMAT) Date date) throws IOException {
        List<Location> locations = jsonParserService.parseJsonArrays(resource.getInputStream(), Location.class);
        locations.forEach(location -> {
            String response = weatherService.getForecast(location).getBody();
            populateForecast(location, date, response);
        });
        Optional<Location> bestLocation = getBestWindsurfingConditions(locations);
        if (bestLocation.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bestLocation.get());
    }

    private Optional<Location> getBestWindsurfingConditions(List<Location> locationList) {
        return locationList.stream().filter(location -> location.getForecast().isSuitableForWindsurfing())
                           .max(Comparator.comparing(location -> location.getForecast().calculateWindsurfingScore()));
    }

    private void populateForecast(Location location, Date date, String response) {
        String dateStr = new SimpleDateFormat(DATE_FORMAT).format(date);
        List<JsonNode> nodes = jsonParserService.getJsonNodes("data", response);
        List<JsonNode> filteredNodes = jsonParserService.filterJsonNodes("datetime", dateStr, nodes);
        if (filteredNodes.isEmpty()) {
            throw new RuntimeException(String.format("No forecast for a given day: %s", dateStr));
        }
        double wind = filteredNodes.get(0).get("wind_spd").asDouble();
        double temperature = filteredNodes.get(0).get("temp").asDouble();
        location.setForecast(new Forecast(wind, temperature));
    }

}

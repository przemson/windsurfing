package pl.trzaskala.windsurfing.controller;

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
import pl.trzaskala.windsurfing.model.Location;
import pl.trzaskala.windsurfing.service.ForecastProcessor;
import pl.trzaskala.windsurfing.service.JsonParserService;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/weather")
public class WindsurfingWeatherController {
    private final JsonParserService jsonParserService;
    private final ForecastProcessor forecastProcessor;

    @Value("classpath:locations.json")
    private Resource resource;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public WindsurfingWeatherController(JsonParserService jsonParserService, ForecastProcessor forecastProcessor) {
        this.jsonParserService = jsonParserService;
        this.forecastProcessor = forecastProcessor;
    }

    @GetMapping(value = "/windsurfing-locations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get best windsurfing location based on weather forecast for a given date",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    public ResponseEntity<Location> getBestWindsurfingLocation(
            @RequestParam(value = "date") @DateTimeFormat(pattern = DATE_FORMAT) Date date)
            throws IOException {
        List<Location> locations = jsonParserService.parseJsonArrays(resource.getInputStream(), Location.class);
        Optional<Location> bestLocation = forecastProcessor.getBestWindsurfingLocation(locations, date);
        if (bestLocation.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bestLocation.get());
    }

}

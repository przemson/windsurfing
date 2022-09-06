package pl.trzaskala.windsurfing.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.trzaskala.windsurfing.model.Forecast;
import pl.trzaskala.windsurfing.model.Location;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static pl.trzaskala.windsurfing.controller.WindsurfingWeatherController.DATE_FORMAT;

@Service
public class ForecastProcessor {

    private final JsonParserService jsonParserService;
    private final WeatherService<Location> weatherService;

    @Autowired
    public ForecastProcessor(JsonParserService jsonParserService, WeatherService<Location> weatherService) {
        this.jsonParserService = jsonParserService;
        this.weatherService = weatherService;
    }

    public Optional<Location> getBestWindsurfingLocation(List<Location> locationList, Date date) {
        doExternalApiCalls(locationList, location -> response -> populateForecast(location, date,
                response.getBody()), location1 -> () -> weatherService.getForecast(location1));
        return locationList.stream().filter(location -> location.getForecast().isSuitableForWindsurfing())
                .max(Comparator.comparing(location -> location.getForecast().calculateWindsurfingScore()));
    }

    private void doExternalApiCalls(List<Location> locations, Function<Location, Consumer<ResponseEntity<String>>> locationConsume,
                                    Function<Location, Supplier<ResponseEntity<String>>> locationSupply) {
        List<CompletableFuture<Void>> apiCalls = new ArrayList<>();
        locations.forEach(
                location -> apiCalls.add(CompletableFuture.supplyAsync(locationSupply.apply(location))
                        .thenAccept(locationConsume.apply(location))));
        CompletableFuture.allOf(apiCalls.toArray(new CompletableFuture[0])).join();
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

package pl.trzaskala.windsurfing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.trzaskala.windsurfing.client.RestClient;
import pl.trzaskala.windsurfing.model.Location;

@Service
@PropertySource("classpath:app.properties")
public class WindsurfingWeatherService implements WeatherService<Location> {

    private final RestClient client;
    private static final String API_URL = "https://api.weatherbit.io/v2.0/forecast/daily?";

    @Value("${api_key}")
    private String API_KEY;
    private static final String QUERY_PARAM_LAT = "lat";
    private static final String QUERY_PARAM_LON = "lon";
    private static final String QUERY_PARAM_KEY = "key";

    public WindsurfingWeatherService(RestClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<String> getForecast(Location location) {
        String url = createUrl(location);
        return client.doGet(url, String.class);
    }

    private String createUrl(Location location) {
        return UriComponentsBuilder.fromHttpUrl(API_URL)
                                   .queryParam(QUERY_PARAM_LAT, location.getLatitude())
                                   .queryParam(QUERY_PARAM_LON, location.getLongitude())
                                   .queryParam(QUERY_PARAM_KEY, API_KEY).build().toString();
    }

}

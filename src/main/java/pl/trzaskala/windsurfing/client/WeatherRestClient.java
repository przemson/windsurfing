package pl.trzaskala.windsurfing.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherRestClient implements RestClient {

    final RestTemplate restTemplate;

    @Autowired
    WeatherRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public <T> ResponseEntity<T> doGet(String url, Class<T> type) {
        return restTemplate.getForEntity(url, type);
    }
}

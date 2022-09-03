package pl.trzaskala.windsurfing.client;

import org.springframework.http.ResponseEntity;

public interface RestClient {

    /**
     * @param url url
     * @param responseType response Type
     * @return response
     */
    <T> ResponseEntity<T> doGet(String url, Class<T> responseType);
}

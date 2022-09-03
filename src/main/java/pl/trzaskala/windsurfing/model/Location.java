package pl.trzaskala.windsurfing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
    private final String city;
    private final String country;
    private final String lat;
    private final String lon;
    @JsonProperty(value = "forecast")
    private Forecast forecast;

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    @JsonCreator
    public Location(@JsonProperty("lat") String lat, @JsonProperty("lon") String lon,
                    @JsonProperty("city") String city, @JsonProperty("country") String country) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    @JsonIgnore
    public String getLatitude() {
        return lat;
    }

    @JsonIgnore
    public String getLongitude() {
        return lon;
    }
}

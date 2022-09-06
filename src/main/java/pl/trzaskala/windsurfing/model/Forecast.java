package pl.trzaskala.windsurfing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Forecast {
    private final double windSpeed;
    private final double temperature;

    @JsonCreator
    public Forecast(@JsonProperty("wind-speed") double windSpeed, @JsonProperty("temperature") double temperature) {
        this.windSpeed = windSpeed;
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    @JsonIgnore
    public boolean isSuitableForWindsurfing() {
        return getWindSpeed() >= 5 && getWindSpeed() <= 18 &&
                getTemperature() >= 5 && getTemperature() <= 35;
    }

    public double calculateWindsurfingScore() {
        return getWindSpeed() * 3 + getTemperature();
    }
}

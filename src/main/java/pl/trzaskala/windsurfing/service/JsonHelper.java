package pl.trzaskala.windsurfing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import pl.trzaskala.windsurfing.model.Forecast;
import pl.trzaskala.windsurfing.model.Location;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class JsonHelper {
    private static final ObjectMapper mapper;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JsonHelper.class);

    static {
        mapper = new ObjectMapper();
    }

    private JsonHelper() {
    }

    public static List<Location> readLocations(InputStream file) {
        List<Location> locations = new ArrayList<>();
        try {
            locations = Arrays.asList(mapper.readValue(file, Location[].class));
        } catch (IOException e) {
            logger.error("Could not read file");
            e.printStackTrace();
        }
        return locations;
    }

    public static Forecast extractForecastForDate(Date date, String dateFormat, String forecastData) {
        Forecast forecast = null;
        try {
            Iterator<JsonNode> iterator = mapper.readTree(forecastData).get("data").elements();
            List<JsonNode> dateForecastJsonNodes = new ArrayList<>();
            iterator.forEachRemaining(dateForecastJsonNodes::add);
            String dateStr = new SimpleDateFormat(dateFormat).format(date);

            dateForecastJsonNodes =
                    dateForecastJsonNodes.stream().filter(n -> n.get("datetime").asText().equals(dateStr))
                                         .collect(Collectors.toList());
            JsonNode node = dateForecastJsonNodes.get(0);
            double wind = node.get("wind_spd").asDouble();
            double temperature = node.get("temp").asDouble();
            forecast = new Forecast(wind, temperature);
        } catch (JsonProcessingException e) {
            logger.error(String.format("Error while processing forecast response for date: %s", date));
            e.printStackTrace();
        }
        return forecast;
    }
}

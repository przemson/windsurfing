package pl.trzaskala.windsurfing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JsonParserService {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JsonParserService.class);

    public <T> List<T> parseJsonArrays(InputStream jsonFile, Class<T> clazz) throws IOException {
        CollectionType listType =
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return mapper.readValue(jsonFile, listType);
    }

    public List<JsonNode> getJsonNodes(String jsonElem, String jsonInput) {
        Iterator<JsonNode> iterator = null;
        try {
            iterator = mapper.readTree(jsonInput).get(jsonElem).elements();
        } catch (JsonProcessingException e) {
            logger.error(String.format("Error while processing json element: %s", jsonElem));
        }
        List<JsonNode> jsonNodes = new ArrayList<>();
        Objects.requireNonNull(iterator).forEachRemaining(jsonNodes::add);
        return jsonNodes;
    }

    public List<JsonNode> filterJsonNodes(String jsonElem, String filteringValue, List<JsonNode> jsonNodes) {
        return jsonNodes.stream().filter(n -> n.get(jsonElem).asText().equals(filteringValue))
                                    .collect(Collectors.toList());
    }
}

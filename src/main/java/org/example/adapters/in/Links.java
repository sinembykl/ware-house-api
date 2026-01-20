package org.example.adapters.in;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class Links {
    // This map stores the actual links
    private Map<String, String> linkMap = new HashMap<>();

    public Links add(String rel, String href) {
        linkMap.put(rel, href);
        return this;
    }

    // This annotation tells Jackson to use the map directly as the JSON value
    @JsonValue
    public Map<String, String> getLinkMap() {
        return linkMap;
    }
}
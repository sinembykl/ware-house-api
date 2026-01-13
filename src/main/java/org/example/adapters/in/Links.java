package org.example.adapters.in;

import java.util.HashMap;
import java.util.Map;

public class Links {
    public Map<String, String> links = new HashMap<>();

    public Links add(String rel, String href) {
        links.put(rel, href);
        return this;
    }
}

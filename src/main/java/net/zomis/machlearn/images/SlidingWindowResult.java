package net.zomis.machlearn.images;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SlidingWindowResult {

    private final Map<ZPoint, Set<Object>> points;

    public SlidingWindowResult(Map<ZPoint, Set<Object>> foundObjects) {
        this.points = new HashMap<>(foundObjects);
    }

    public Map<ZPoint, Set<Object>> getPoints() {
        return new HashMap<>(points);
    }

}

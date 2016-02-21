package net.zomis.machlearn.text;

import java.util.stream.IntStream;

public class TextFeatureMapper {
    
    private static final String[] CONTAINS = {
        "better fit", "better suited", "better place",
        "close", "off-topic", "design", "whiteboard", "this question", "this site",
        "programmers.se", "help at", "place to ask", "migrate", "belong",
        "instead", "the place for", "try programmers", "for programmers",
        "on programmers", "at programmers", "to programmers" };

    public double[] toFeatures(String str) {
        return IntStream.range(0, CONTAINS.length)
            .mapToDouble(i -> str.contains(CONTAINS[i]) ? 1 : 0)
            .toArray();
    }

}

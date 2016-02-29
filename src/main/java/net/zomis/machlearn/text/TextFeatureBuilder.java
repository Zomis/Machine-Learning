package net.zomis.machlearn.text;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class TextFeatureBuilder {

    public static final Comparator<Map.Entry<String, Integer>> SORT_BY_VALUE =
        Comparator.<Map.Entry<String, Integer>, Integer>comparing(Map.Entry::getValue)
            .reversed();

    private final int[] nGrams;
    private final Map<String, Integer> counts;
    private final BiPredicate<String, Integer> featureFilter;

    public TextFeatureBuilder(int[] nGrams, BiPredicate<String, Integer> featureFilter) {
    	System.out.println(nGrams.toString());
        if (nGrams != null && nGrams.length < 0) {
            throw new IllegalArgumentException("nGrams must have at least one value");
        }
        this.nGrams = nGrams;
        this.counts = new HashMap<>();
        this.featureFilter = featureFilter;
    }

    public void add(String processed) {
        List<String> sections = Arrays.asList(processed.split(" "));
        sections = sections.stream().filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
        for(int n : nGrams){
	        for (int i = 0; i <= sections.size() - n; i++) {
	            List<String> values = sections.subList(i, i + n);
	            String value = String.join(" ", values).trim();
	            if (value.isEmpty()) {
	                continue;
	            }
	            if (featureFilter.test(value,n)) {
	            	//System.out.println(value);
	                counts.merge(value, 1, Integer::sum);
	            }
	            /*if (featureFilter.test(value)) {
	            	System.out.println(value);
	                counts.merge(value, 1, Integer::sum);
	            }*/
	        }
        }
    }

    public TextFeatureMapper mapper(int maxLimit) {
        String[] features = counts.entrySet().stream()
            .sorted(SORT_BY_VALUE)
            .limit(maxLimit).map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .toArray(new String[maxLimit]);
        	System.out.println("=====================================>"+features);
        return new TextFeatureMapper(features);
    }

    public Map<String, Integer> getCounts() {
        return new HashMap<>(counts);
    }
}

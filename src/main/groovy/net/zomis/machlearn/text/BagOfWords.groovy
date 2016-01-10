package net.zomis.machlearn.text

class BagOfWords {
    private final Map<String, Integer> counts = [:]

    String[] addText(String s) {
        String cleaned = s.toLowerCase().replaceAll('[\'"\\.\\(\\),]', ' ')
        String[] split = cleaned.split(' ')
        for (String key : split) {
            if (!key.isEmpty()) {
                counts.merge(key, 1, {a, b -> a + b})
            }
        }
        split
    }

    Map<String, Integer> getData() {
        new HashMap<String, Integer>(counts)
    }
}

package net.zomis.machlearn.text.duga;

import net.zomis.machlearn.common.ClassifierFunction;
import net.zomis.machlearn.common.LearningDataSet;
import net.zomis.machlearn.common.PrecisionRecallF1;
import net.zomis.machlearn.images.MyGroovyUtils;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.regression.ConvergenceIterations;
import net.zomis.machlearn.regression.GradientDescent;
import net.zomis.machlearn.regression.LogisticRegression;
import net.zomis.machlearn.text.BagOfWords;
import net.zomis.machlearn.text.TextFeatureBuilder;
import net.zomis.machlearn.text.TextFeatureMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProgrammersCommentTest {

    private static final Pattern PROG_LINK = Pattern.compile(Pattern.quote("<a href=\"http") + "s?"
            + Pattern.quote("://programmers.stackexchange.com")
            + "(/|/help/.*)?" + Pattern.quote("\">"));

    @Test
    public void commentLearning() {
        String source = MyGroovyUtils.text(getClass().getClassLoader()
            .getResource("trainingset-programmers-comments.txt"));
        String[] lines = source.split("\n");
        TextFeatureBuilder textFeatures = new TextFeatureBuilder(2, this::filter);


        TextFeatureMapper oldMapper = new TextFeatureMapper(
                "better fit", "better suited", "better place",
                "close", "off-topic", "design", "whiteboard", "this question", "this site",
                "programmers.se", "help at", "place to ask", "migrate", "belong",
                "instead", "the place for", "try programmers", "for programmers",
                "on programmers", "at programmers", "to programmers");

        LearningDataSet data = new LearningDataSet();
        List<String> processedStrings = new ArrayList<>();
        for (String str : lines) {
            if (!str.startsWith("0 ") && !str.startsWith("1 ")) {
                continue;
            }
            boolean expected = str.startsWith("1");
            String text = str.substring(2);
            String processed = preprocess(text);
            char expectedChar = expected ? '1' : '0';
            processedStrings.add(expectedChar + processed);
            textFeatures.add(processed);
            // println text
        }

        TextFeatureMapper mapper = textFeatures.mapper(50);
        System.out.println("Counts:");
        textFeatures.getCounts().entrySet().stream()
            .sorted(TextFeatureBuilder.SORT_BY_VALUE)
            .forEach(System.out::println);
        System.out.println();
        System.out.println();
        System.out.println("Mapper features:");
        System.out.println(Arrays.toString(mapper.getFeatures()));

        for (String str : processedStrings) {
            boolean expectTrue = str.charAt(0) == '1';
            data.add(str, mapper.toFeatures(str), expectTrue ? 1 : 0);
        }

        System.out.println("Data is:");
        data.getData().stream().forEach(System.out::println);

        double[] learnedTheta = GradientDescent.gradientDescent(
            LogisticRegression.costFunction(data.getXs(), data.getY()),
            new ConvergenceIterations(20000),
            new double[data.numFeaturesWithZero()], 0.01);
        double cost = LogisticRegression.costFunction(data.getXs(), data.getY()).apply(learnedTheta);
        System.out.println("Cost: " + cost);

        ClassifierFunction function = (theta, x) ->
                LogisticRegression.hypothesis(theta, x) >= 0.3;

        PrecisionRecallF1 score = data.precisionRecallF1(learnedTheta, function);
        System.out.println(score);

        System.out.println("False negatives:");
        data.stream()
            .filter(LearningData::getOutputBoolean)
            .filter(d -> !function.classify(learnedTheta, d.getInputs()))
            .forEach(d -> System.out.println(d.getForData()));
    }

    private boolean filter(String feature) {
        return feature.length() > 7;
    }

    private String preprocess(String text) {
        text = PROG_LINK.matcher(text).replaceAll("(link-to-programmers)");

        text = text.replaceAll("<a href=\"([^\"]+)\">", "$1 "); // Extract links
        text = text.replaceAll("<[^<>]+>", " "); // Remove HTML
        text = text.replaceAll("\\d+", "(number)");
        text = text.replaceAll("stack overflow", "stackoverflow");
        text = text.replaceAll("stack exchange", "stackexchange");
        text = text.replaceAll("programmers.stackexchange.com/q", "(progs-question) ");
        text = text.replaceAll("programmers.stackexchange.com/t", "(progs-tag) ");
        text = text.replaceAll("programmers.stackexchange.com/a", "(progs-answer) ");
        text = text.replaceAll("(http|https)://[^\\s]*", "(unclassified-httpaddr)");
        text = text.replaceAll("[\\.,]", " ");
        text = text.replaceAll("\\(number\\) (secs?|mins?) ago", "");
        return text.toLowerCase().replace("\"", "");
    }

}
